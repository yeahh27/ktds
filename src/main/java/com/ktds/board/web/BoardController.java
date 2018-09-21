package com.ktds.board.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ktds.board.service.BoardService;
import com.ktds.board.vo.BoardSearchVO;
import com.ktds.board.vo.BoardVO;
import com.ktds.common.exceptions.PolicyViolationException;
import com.ktds.common.session.Session;
import com.ktds.common.web.DownloadUtil;
import com.ktds.member.vo.MemberVO;

import io.github.seccoding.web.pager.explorer.PageExplorer;

@Controller
public class BoardController {
	
	//private Logger logger = LoggerFactory.getLogger(BoardController.class);
	private Logger statisticslogger = LoggerFactory.getLogger("list.statistics");
	private Logger paramLogger = LoggerFactory.getLogger(BoardController.class);
	
	@Value("${upload.path}")      // properties data 가져올때
	private String uploadPath;
	
	@Autowired
	@Qualifier("boardServiceImpl")
	private BoardService boardService;
	
	@RequestMapping("/board/list/init")
	public String viewBoardListPageForInitiate(HttpSession session) {
		session.removeAttribute(Session.SEARCH);
		return "redirect:/board/list";
	}
	
	@RequestMapping("/board/list")
	public ModelAndView viewBoardListPage(@ModelAttribute BoardSearchVO boardSearchVO, HttpServletRequest request
										  , HttpSession session ) {					// @ModelAttribute BoardSearchVO boardSearchVO: 페이지 번호 전달
		
		/*List<BoardVO> boardVOList = this.boardService.readAllBoards();
		
		statisticslogger.info("URL : /board/list, IP : " + request.getRemoteAddr() + ", List Size : " + boardVOList.size());
		
		ModelAndView view = new ModelAndView("board/list");
		view.addObject("boardVOList", boardVOList);*/
		
		// 전체검색 or 상세 -> 목록 or 글쓰기
		if(boardSearchVO.getSearchKeyword() == null) {
			boardSearchVO = (BoardSearchVO) session.getAttribute(Session.SEARCH);
			if(boardSearchVO == null) {
				boardSearchVO = new BoardSearchVO();
				boardSearchVO.setPageNo(0);
			}
		}

		PageExplorer pageExplorer = this.boardService.readAllBoards(boardSearchVO);
		
		statisticslogger.info("URL : /board/list, IP : " + request.getRemoteAddr() + ", List Size : " + pageExplorer.getList().size());
		
		session.setAttribute(Session.SEARCH, boardSearchVO);
		
		ModelAndView view = new ModelAndView("board/list");
		view.addObject("boardVOList", pageExplorer.getList());
		view.addObject("pagenation", pageExplorer.make());
		view.addObject("size", pageExplorer.getTotalCount());
		view.addObject("boardSearchVO", boardSearchVO);
		
		return view;
	}
	
	/*@RequestMapping("/detail")				// Get, Post 둘다 처리
	public ModelAndView viewBoardDetailPage() {
		ModelAndView view = new ModelAndView("board/detail");
		view.addObject("message","Hello, Spring");
		view.addObject("name", "Spring Web MVC Framework");
		view.addObject("age", 100);
		view.addObject("isAge", false);
		return view;
	}*/
	
	// Spring 4.2 이하에서 사용.
	// @RequestMapping(value="/write", method=RequestMethod.GET)		// Servlet의 doGet
	// Spring 4.3 이상에서 사용.
	@GetMapping("/board/write")
	public String viewBoardWritePage() {
		return "board/write";
	}
	/*@GetMapping("/board/write")
	public String viewBoardWritePage(@SessionAttribute(name="_USER_", required=false) MemberVO memberVO) {
		
		if(memberVO == null) {		// 로그인을 하지 않았다면
			return "redirect:/member/login";
		}			
		
		return "board/write";
	}*/
	
	@PostMapping("/board/write")
	public ModelAndView doBoardWriteAction(@Valid @ModelAttribute BoardVO boardVO, Errors errors,
									HttpServletRequest request, HttpSession session) {
//		boolean isSuccess = this.boardService.createBoard(boardVO);
//		return "redirect:/list";		// list로 가랏
		
		ModelAndView view = new ModelAndView("redirect:/board/list/init");
		
		// Validation Annotation이 실패했는지 체크
		if (errors.hasErrors()) {
			view.setViewName("board/write");		// view path 지정
			view.addObject("boardVO", boardVO);
			return view;
		}
		
		
		MultipartFile uploadFile = boardVO.getFile();
		
		if(!uploadFile.isEmpty()) {
			// 실제 파일 이름
			String originFileName = uploadFile.getOriginalFilename();
			// 파일 시스템에 저장될 파일의 이름(난수)
			String fileName = UUID.randomUUID().toString();
			
			// 폴더가 존재하지 않는다면, 생성
			File uploadDir = new File(this.uploadPath);
			if(!uploadDir.exists()) {
				uploadDir.mkdirs();
			}
			
			// 파일이 업로드될 경로 지정
			File destFile = new File(this.uploadPath, fileName);
			
			try {
				// 업로드
				uploadFile.transferTo(destFile);
				// DB에 File 정보 저장하기 위한 정보 셋팅
				boardVO.setOriginFileName(originFileName);
				boardVO.setFileName(fileName);
			} catch (IllegalStateException | IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
		MemberVO loginMemberVO = (MemberVO) session.getAttribute(Session.USER);	// Object 타입이기 때문에 casting이 반드시 필요
		String email = loginMemberVO.getEmail();
		boardVO.setMemberVO(loginMemberVO);
		boardVO.setEmail(email);
		
		//String view = this.boardService.createBoard(boardVO, loginMemberVO) ? "redirect:/board/list" : "redirect:/board/write";
		boolean isSuccess = this.boardService.createBoard(boardVO, loginMemberVO);
		
		String paramFormat = "IP:%s, Param:%s, Result:%s";
		paramLogger.debug( String.format(paramFormat, request.getRemoteAddr()
				, boardVO.getSubject() + ", " + boardVO.getContent() + ", " + boardVO.getEmail() + ", " 
						+ boardVO.getFileName() + ", " + boardVO.getOriginFileName()
				, view.getViewName()) );		// view.getViewName() == redirect:/board/list
		
		return view;
		//return this.boardService.createBoard(boardVO, loginMemberVO) ? "redirect:/board/list" : "redirect:/board/write"; 	// 삼항연산자 (Elvis Operator)
	}
	
	
	/*@PostMapping("/write")
	public String doBoardWriteAction(HttpServletRequest request) {
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		String email = request.getParameter("email");
		
		BoardVO boardVO = new BoardVO();
		boardVO.setSubject(subject);
		boardVO.setContent(content);
		boardVO.setEmail(email);
		
		boolean isSuccess = this.boardService.createBoard(boardVO);
		
		return "redirect:/list";		// list로 가랏
	}*/
	
	// http://localhost:8080/HelloSpring/board/detail?id=1
	/*@RequestMapping("/board/detail")
	public ModelAndView viewBoardDetailPage(@RequestParam int id) {
		BoardVO boardVO = this.boardService.selectOneBoard(id);
		
		ModelAndView view = new ModelAndView();
		view.addObject("boardVO", boardVO);
		return view;
	}*/
	
	// http://localhost:8080/HelloSpring/board/detail/1
	@RequestMapping("/board/detail/{id}")
	public ModelAndView viewBoardDetailPage(@PathVariable int id, @SessionAttribute(Session.USER) MemberVO memberVO, HttpServletRequest request ) {
		/*if(memberVO.getPoint() < 2) {
			return new ModelAndView("redirect:/board/list");
		}*/
		
		BoardVO boardVO = this.boardService.readOneBoard(id, memberVO);;
		
		ModelAndView view = new ModelAndView("board/detail");
		view.addObject("boardVO", boardVO);
		
		String paramFormat = "IP:%s, Param:%s, Result:%s";
		paramLogger.debug( String.format(paramFormat, request.getRemoteAddr(), id
							, boardVO.getId() + ", " + boardVO.getSubject() + ", " + boardVO.getContent() + ", " + boardVO.getEmail() + ", " 
								+ boardVO.getFileName() + ", " + boardVO.getOriginFileName()) );
		
		return view;
	}
	
	
	@RequestMapping("/board/delete/{id}")
	public String doBoardDeleteAction(@PathVariable int id, HttpServletRequest request, @SessionAttribute(Session.USER) MemberVO memberVO) {
		boolean isSuccess = this.boardService.deleteBoard(id);
		
		String paramFormat = "IP:%s, Actor:%s, Param:%s, Result:%s";
		paramLogger.debug( String.format(paramFormat, request.getRemoteAddr()
							, memberVO.getEmail()
							, id
							, isSuccess) );
		
		return "redirect:/board/list";
	}
	
	@RequestMapping("/board/download/{id}")
	public void fileDownload(@PathVariable int id, HttpServletRequest request, HttpServletResponse response
							, @SessionAttribute(Session.USER) MemberVO memberVO) {
		
		if(memberVO.getPoint() < 5) {
			throw new PolicyViolationException("다운로드를 위한 포인트가 부족합니다.", "/board/detail/" + id);
		}
		
		BoardVO boardVO = this.boardService.readOneBoard(id);
		String originFileName = boardVO.getOriginFileName();
		String fileName = boardVO.getFileName();
		
		// Windows \
		// Unix/Linux/macos /
		try {
			new DownloadUtil(this.uploadPath + File.separator + fileName).download(request, response, originFileName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
	}
}
