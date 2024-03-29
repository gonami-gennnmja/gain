package org.edu.controller;

import java.util.List;

import javax.inject.Inject;

import org.edu.dao.IF_MemberDAO;
import org.edu.vo.ChartVO;
import org.edu.vo.MemberVO;
import org.edu.vo.PageVO;
import org.hsqldb.lib.SimpleLog;
import org.jboss.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsonDataController {

	@Inject
	private IF_MemberDAO memberDAO;
	private Logger logger = Logger.getLogger(SimpleLog.class);
	
	//RestAPI서버: html 오픈차트 js 사용해서 기존 투표한 자료 가져오기
	@RequestMapping(value="/chart/getdata", method = RequestMethod.GET)
	public ResponseEntity<ChartVO> getData() {
		ResponseEntity<ChartVO> entity = null;
		try {
		ChartVO chartVO = memberDAO.getData();
		entity = new ResponseEntity<>(chartVO, HttpStatus.OK);//200
		} catch(Exception e) {
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);//400
		}
		return entity;
	}
	
	
	//RestAPI인증 서버: 안드로이드앱에서 회원목록 중 선택한 id 삭제
	@RequestMapping(value = "/android/delete/{user_id}" , method = RequestMethod.POST)
	public ResponseEntity<String> androidDelete(@PathVariable("user_id")String user_id) {
		ResponseEntity<String> entity = null;
		try {
			memberDAO.deleteMember(user_id);
			entity = new ResponseEntity<>(HttpStatus.OK);//200
		} catch (Exception e) {
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST); //400
		}
		return entity;
	}
	//RestAPI인증 서버(아래): 안드로이드 앱에서 로그인에 사용
	@RequestMapping(value = "/android/login", method = RequestMethod.POST)
	public ResponseEntity<MemberVO> androidLogin(@RequestParam("txtUsername") String user_id, @RequestParam("txtPassword") String user_pw) {
		ResponseEntity<MemberVO> entity = null;
		try {
			MemberVO memberVO = memberDAO.readMember(user_id);
			String bcryptPassword = memberVO.getUser_pw();
			//스프링 시큐리티 4.x BCryptPasswordEncoder로 암호화 된 값을 비교
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);//()속 숫자는 암호화 강도(10은 강약 정도)
			if (passwordEncoder.matches(user_pw, bcryptPassword)) {
				logger.debug("계정정보 일치");
				entity = new ResponseEntity<>(memberVO, HttpStatus.OK);//code 200
			}
			else {
				logger.debug("계정정보 불일치");
				entity = new ResponseEntity<>(HttpStatus.NO_CONTENT);//code 204
			}
		} catch (Exception e) {
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);//code 400
		}
		return entity;//json(key:value)데이터로 반환값 리턴
	}
	
	//RestAPI서버(아래): 회원목록을 출력하는 기능
	@RequestMapping(value = "/android/list", method = RequestMethod.POST)
	public ResponseEntity<List<MemberVO>> androidMember() {
		ResponseEntity<List<MemberVO>> entity = null;
		PageVO pageVO = new PageVO();
		pageVO.setPage(1);
		pageVO.setPerPageNum(10);
		pageVO.setqueryPerPageNum(1000);//1회 쿼리에서 1000명 허용
		try {
			entity = new ResponseEntity<>(memberDAO.selectMember(pageVO), HttpStatus.OK);
		} catch (Exception e) {
			entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return entity;
	}
}
