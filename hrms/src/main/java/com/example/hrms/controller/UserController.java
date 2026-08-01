package com.example.hrms.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hrms.model.AppliedJob;
import com.example.hrms.model.JobInfo;
import com.example.hrms.model.Response;
import com.example.hrms.model.User;
import com.example.hrms.repo.AppliedJobRepo;
import com.example.hrms.repo.JobInfoRepo;
import com.example.hrms.repo.ResponseRepo;
import com.example.hrms.repo.UserRepo;
import com.exmaple.hrms.dto.ResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	@Autowired
	JobInfoRepo jrepo;
	@Autowired
	UserRepo urepo;
	@Autowired
	ResponseRepo rrepo;
	@Autowired
	AppliedJobRepo ajrepo;
	
	@GetMapping("user/userdash")
	public String userDashboard(HttpSession session) {
		if(session.getAttribute("user") == null) {
			return "redirect:/login";
		}
		return "user/userdash";
	}
	@GetMapping("/user/viewjobs")
	public String viewJobs(HttpSession session, Model model) {
		if(session.getAttribute("user")==null) {
			return "redirect:/login";
		}
		List<JobInfo> jinfo=jrepo.findAll();
		model.addAttribute("jinfo",jinfo);
		return "/user/viewjobs";
	}
	@GetMapping("/user/changepwd")
	public String changepwd(HttpSession session) {
		if(session.getAttribute("user")==null) {
			return "redirect:/login";
		}
		return "user/changepwd";
	}
	@GetMapping("/user/logout")
	public String logout(HttpSession session) {
		if(session.getAttribute("user")==null) {
			return "redirect:/login";
		}
		session.removeAttribute("user");
		return "redirect:/login";
	}
	@PostMapping("/user/changepwd")
	public String changepwd(HttpSession session, HttpServletRequest request, RedirectAttributes attrib) {
		if(session.getAttribute("user")==null) {
			return"redirect:/login";
		}
		String oldpassword=request.getParameter("oldpassword");
		String newpassword=request.getParameter("newpassword");
		String confirmpassword=request.getParameter("confirmpassword");
		if (!newpassword.equals(confirmpassword)) {
			attrib.addFlashAttribute("msg","Newpassword and Confirm password are not matched");
			return "redirect:/user/changepwd";
		}
		try {
			User user=(User)session.getAttribute("user");
			if(!user.getPassword().equals(oldpassword)) {
				attrib.addFlashAttribute("msg","oldpassword is not matched");
				return "redirect:/user/changepwd";
			}
			user.setPassword(newpassword);
			urepo.save(user);
			return "redirect:/user/logout";
		}
		catch(Exception e) {
			attrib.addFlashAttribute("msg","UserId not matched");
			return "redirect:/user/changepwd";
		}
	}
	@GetMapping("/user/giveresponse")
	public String showGiveResponse(Model model, HttpSession session) {
		if(session.getAttribute("user")==null) {
			return "redirect:/login";
		}
		ResponseDto rdto=new ResponseDto();
		model.addAttribute("rdto", rdto);
		return "user/giveresponse";
		
	}
	@PostMapping("/user/giveresponse")
	public String giveResponse(HttpSession session, @ModelAttribute ResponseDto rdto, RedirectAttributes attrib) {
		if(session.getAttribute("user")==null) {
			return "redirect:/login";
		}
		Response response=new Response();
		response.setResponsetype(rdto.getResponsetype());
		response.setSubject(rdto.getSubject());
		response.setResponsetext(rdto.getResponsetext());
		  Date dt=new Date();		
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
		String posteddate=sdf.format(dt);
		response.setPostdate(posteddate);
		User user=(User) session.getAttribute("user");
		response.setName(user.getName());
		response.setContactno(user.getContactno());
		rrepo.save(response);
		attrib.addFlashAttribute("msg","Your response is submitted");
		return "redirect:/user/giveresponse";
	}
	@GetMapping("/user/viewprofile")
	public String viewProfile(HttpSession session, Model model) {
		if(session.getAttribute("user")==null) {
			return "redirect:/login";
		}
		User user=(User) session.getAttribute("user");
		model.addAttribute("user", user);
		return "/user/viewprofile";
	}
	@GetMapping("/user/applyjob/{id}")
	public String applyJob(@PathVariable("id") int id, HttpSession session, RedirectAttributes attrib) {
		if(session.getAttribute("user")==null) {
			return "redirect:/login";
		}
		User user=(User) session.getAttribute("user");
		String emailaddress=user.getEmailaddress();
		if(ajrepo.existsByJobidAndEmailaddress(id, emailaddress)==true) {
			
			attrib.addFlashAttribute("msg", "You have already applied for this job.");
			return "redirect:/user/viewjobs";
		}
		JobInfo ji=jrepo.findById(id).get();
		AppliedJob aj=new AppliedJob();
		aj.setJobid(ji.getId());
		aj.setTitle(ji.getTitle());
		aj.setDescriprrtion(ji.getDescription());
		aj.setName(user.getName());
		aj.setContactno(user.getContactno());
		aj.setEmailaddress(user.getEmailaddress());
		aj.setQualification(user.getQualification());
		aj.setExperience(user.getExperience());
		aj.setKeyskill(user.getKeyskill());
		Date dt=new Date();
		SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
		String applieddate=df.format(dt);
		aj.setApplieddate(applieddate);
		aj.setStatus("true");
		ajrepo.save(aj);
		attrib.addFlashAttribute("msg", "You have successfully applied for the job.");
		return "redirect:/user/viewjobs";
	}
	
  }



