//tao
package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Host;
import com.example.demo.model.HostAccount;
import com.example.demo.repository.HostRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/host")
public class HostAccountController {

	@Autowired
	private HttpSession session;

	@Autowired
	private HostAccount haccount;

	@Autowired
	HostRepository hostrepository;

	@GetMapping({ "/index", "/logout" })
	public String index() {
		//セッション破棄、強制返却処理AOP？
		session.invalidate();
		System.out.println("Hid:"+haccount.getId());
		System.out.println("Hname:"+haccount.getName());

		return "hostLogin";
	}

	@PostMapping("/login")
	public String login(
			@RequestParam(name = "id", required = false) Integer id,
			@RequestParam(name = "password", required = false) String password,
			Model m) {

		if (id == null || password == null || password.equals("")) {
			m.addAttribute("error", "全項目を入力してください");
			return "hostLogin";
		}

		Host host = null;
		Optional<Host> record = hostrepository.findByIdAndPassword(id, password);

		if (record.isPresent()) {
			host = record.get();
		}

		if (host == null) {
			m.addAttribute("error", "管理者IDまたはパスワードが一致していません");
			return "hostLogin";
		}
		
		haccount.setName(host.getName());
		haccount.setId(host.getId());
		System.out.println("Hid:"+haccount.getId());
		System.out.println("Hname:"+haccount.getName());
		
		return "select";//G202

	}
}