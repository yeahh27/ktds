package com.ktds.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ktds.board.dao.BoardDao;
import com.ktds.board.vo.BoardSearchVO;
import com.ktds.board.vo.BoardVO;
import com.ktds.common.exceptions.PolicyViolationException;
import com.ktds.member.biz.MemberBiz;
import com.ktds.member.dao.MemberDao;
import com.ktds.member.vo.MemberVO;
import com.ktds.reply.dao.ReplyDao;
import com.ktds.reply.vo.ReplyVO;

import io.github.seccoding.web.pager.Pager;
import io.github.seccoding.web.pager.PagerFactory;
import io.github.seccoding.web.pager.explorer.ClassicPageExplorer;
import io.github.seccoding.web.pager.explorer.PageExplorer;

@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	@Qualifier("boardDaoImplMyBatis")
	private BoardDao boardDao;				// null -> new나 생성자없이 주소값을 주기 위해서 setter를 사용
	
	/*public void setBoardDao(BoardDao boardDao) {	// main에서 객체를 만들었기 때문에 main에서 호출 (setter)
		System.out.println("Spring에서 호출함");
		System.out.println(boardDao);
		this.boardDao = boardDao;
	}*/
	
	/*public BoardServiceImpl2(BoardDao boradDao) {		// String에서 호출 (생성자)
		this.boradDao = boradDao;
	}*/
	
	@Autowired
	private MemberBiz memberBiz;
	
	@Autowired
	private ReplyDao replyDao;
	
	@Override
	public boolean createBoard(BoardVO boardVO, MemberVO memberVO) {
		
		// 업로드를 했다면,
		boolean isUploadFile = boardVO.getOriginFileName().length() > 0;
		
		int point = 10;
		if(isUploadFile) {
			point += 10;
		}
		
		this.memberBiz.updatePoint(boardVO.getEmail(), point);
		
		int memberPoint = memberVO.getPoint();
		memberPoint += point;
		memberVO.setPoint(memberPoint);
		
		boolean isSuccess = this.boardDao.insertBoard(boardVO) > 0;
		
		//Integer.parseInt("ABC");		// NumberFormatException
		
		return isSuccess;
	}

	@Override
	public boolean updateBoard(BoardVO boardVO) {
		System.out.println("Call BoardService.updateBoard();");
		return this.boardDao.updateBoard(boardVO) > 0;
	}

	@Override
	public BoardVO readOneBoard(int id) {
		return this.boardDao.selectOneBoard(id);
	}
	
	@Override
	public BoardVO readOneBoard(int id, MemberVO memberVO) {
		BoardVO boardVO = this.readOneBoard(id);
		
		List<ReplyVO> replyList = this.replyDao.selectAllReplies(id);
		boardVO.setReplyList(replyList);
		
		if( !boardVO.getEmail().equals(memberVO.getEmail())) {
			
			if(memberVO.getPoint() < 2) {
				throw new PolicyViolationException("포인트가 부족합니다.", "/board/list");
			}
			
			this.memberBiz.updatePoint(memberVO.getEmail(), -2);
			int point = memberVO.getPoint();
			point -= 2;
			memberVO.setPoint(point);
		}
		return boardVO;
	}

	@Override
	public boolean deleteBoard(int id) {
		return this.boardDao.deleteOneBoard(id) > 0;
	}

	/*@Override
	public List<BoardVO> readAllBoards() {
		return this.boardDao.selectAllBoards();
	}*/
	
	@Override
	public PageExplorer readAllBoards(BoardSearchVO boardSearchVO) {
		
		int totalCount = this.boardDao.selectAllBoardsCount(boardSearchVO);
		
		Pager pager = PagerFactory.getPager(Pager.ORACLE, boardSearchVO.getPageNo()+"");	// isDBOracle : oracle인지? (오라클만 사용방법이 달라)
		pager.setTotalArticleCount(totalCount);
		
		PageExplorer pageExplorer = pager.makePageExplorer(ClassicPageExplorer.class, boardSearchVO);
		
		pageExplorer.setList(this.boardDao.selectAllBoards(boardSearchVO));
		
		return pageExplorer;
	}

}
