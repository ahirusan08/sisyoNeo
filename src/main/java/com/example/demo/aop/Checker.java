//tao
package com.example.demo.aop;

import java.time.LocalDateTime;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Rental;
import com.example.demo.model.HostAccount;
import com.example.demo.model.UserAccount;
import com.example.demo.repository.RentalRepository;

import jakarta.servlet.http.HttpSession;

@Aspect
@Component
public class Checker {

	@Autowired
	RentalRepository rentalrepository;

	@Autowired
	HttpSession session;

	@Autowired
	UserAccount useraccount;

	@Autowired
	HostAccount hostaccount;

	//	Rental rental;

	@Before("execution(* com.example.demo.controller.*AccountController.index(..))")
	public void returnBook(JoinPoint jp) {

		LocalDateTime today = LocalDateTime.now();

		List<Rental> list = rentalrepository.findByReturnDateIsNullAndVersionNoLessThanAndLimitDateLessThan(2, today);

		for (Rental rental : list) {

			LocalDateTime limit = rental.getLimitDate();
			rental.setReturnDate(limit);

			Integer vnum = rental.getVersionNo();

			if (vnum == 1) {
				rental.update(1);//管理者は1名なので1
			}

			rentalrepository.saveAndFlush(rental);

		}

	}

	//	@Before("execution(* com.example.demo.controller.HostAccountController.index(..))")
	//	public void trushCan(JoinPoint jp) {
	//
	//		//session.invalidate();
	//		Integer id = useraccount.getId();
	//		String name = useraccount.getName();
	//
	//		System.out.println("id:" + id);
	//		System.out.println("name:" + name);
	//
	//	}

	@Around("within(com.example.demo.controller.BookController)")
	public Object UsercheckLogin(ProceedingJoinPoint jp) throws Throwable {

		Object result = jp.proceed();
		Integer id = useraccount.getId();
		String name = useraccount.getName();

		Integer hid = hostaccount.getId();
		String hname = hostaccount.getName();

		//		if (id == null || name == null) {
		//		
		//			result = "redirect:/user/index";
		//		}

		if ((id != null && name != null) || (hid != null && hname != null)) {
			return result;
		}

		result = "redirect:/user/index";
		return result;
	}

	//	@Around("execution(!* com.example.demo.controller.HostAccountController.index(..)) || "
	//			+ "within(com.example.demo.controller.HostController) || "
	//			+ "within(com.example.demo.controller.UserAccountController) || "
	//			+ "within(com.example.demo.controller.BookController)")
	//	public Object HostcheckLogin(ProceedingJoinPoint jp) throws Throwable {
	//
	//		Object result = jp.proceed();
	//		Integer id = hostaccount.getId();
	//		String name = hostaccount.getName();
	//
	//		System.out.println("id:" + id);
	//		System.out.println("name:" + name);
	//
	//		if (id == null || name == null) {
	//			System.out.println("ZZZ222");
	//			result = "redirect:/host/account/index";
	//		}
	//
	//		return result;
	//	}
}