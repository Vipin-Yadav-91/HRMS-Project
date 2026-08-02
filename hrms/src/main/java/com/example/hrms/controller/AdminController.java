package com.example.hrms.controller;



import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hrms.model.AdminInfo;
import com.example.hrms.model.AppliedJob;
import com.example.hrms.model.Enquiry;
import com.example.hrms.model.JobInfo;
import com.example.hrms.model.Response;
import com.example.hrms.model.User;
import com.example.hrms.repo.AdminInfoRepo;
import com.example.hrms.repo.AppliedJobRepo;
import com.example.hrms.repo.EnquiryRepo;
import com.example.hrms.repo.JobInfoRepo;
import com.example.hrms.repo.ResponseRepo;
import com.example.hrms.repo.UserRepo;
import com.exmaple.hrms.dto.JobInfoDto;
//import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	@Autowired
	UserRepo urepo;
	
	@Autowired
	JobInfoRepo jrepo;
	
	@Autowired
	EnquiryRepo erepo;
	
	@Autowired
	AdminInfoRepo airepo;
	
	@Autowired
	ResponseRepo rrepo;
	
	@Autowired
	AppliedJobRepo ajrepo;
	
	@GetMapping("admin/admindashboard")
	public String showAdminDashboard(HttpSession session,Model model) {
		if(session.getAttribute("admin")==null) {
			
			return "redirect:/adminlogin";
		}
		model.addAttribute("usercounter", urepo.count());
		return "admin/admindashboard";
	}
	@GetMapping("admin/jobseeker")
	public String viewUsers (HttpSession session, Model model) {
		
	if (session.getAttribute("admin")==null) {
		return"redirect:/adminlogin";
	}
	List<User> users=urepo.findAll();
	model.addAttribute("users", users);
	return"admin/jobseeker";
	}
	//@GetMapping("admin/enquiries")
	//public String viewE (HttpSession session, Model model) {
		
	//if (session.getAttribute("admin")==null) {
	//	return"redirect:/adminlogin";
	//}
	//List<User> users=urepo.findAll();
	//model.addAttribute("users", users);
	//return"admin/jobseeker";
	//}
	@GetMapping("/admin/logout")
	public String logout(HttpSession session) {
		if(session.getAttribute("admin")==null) {
			return "redirect:/adminlogin";
		}
		session.invalidate();
		return "redirect:/adminlogin";
	}
	@GetMapping("/admin/postjob")
	public String showPostJob(Model model, HttpSession session) {
		if (session.getAttribute("admin")==null) {
			return "redirect:/adminlogin";
		}
		JobInfoDto jdto=new JobInfoDto();
		model.addAttribute("jdto", jdto);
		return "admin/postjob";
	}
	
	
	
	@PostMapping("/admin/postjob")
	public String saveJob(@ModelAttribute JobInfoDto jdto, HttpSession session, RedirectAttributes attrib) {
		if(session.getAttribute("admin")==null) {
			return "redirect:/adminlogin";
		}
		JobInfo ji=new JobInfo();
		ji.setTitle(jdto.getTitle());
		ji.setDescription(jdto.getDescription());
		ji.setLocation(jdto.getLocation());
		ji.setSalary(jdto.getSalary());
		ji.setJobtype(jdto.getJobtype());
		ji.setLastdate(jdto.getLastdate());
		String posteddate = new Date().toString();
		ji.setPostdate(posteddate);
		jrepo.save(ji);
		attrib.addFlashAttribute("msg","Job details is posted");
		return "redirect:/admin/postjob";
	}
	
	@GetMapping("/admin/viewjob")
	public String showPostedJobs(Model model, HttpSession session) {

	    List<JobInfo> postedJobsList = jrepo.findAll();
	    model.addAttribute("postedJobs", postedJobsList);

	    return "admin/postedJobs";
	}
	@GetMapping("/admin/enquiries")
	public String viewEnquiries(HttpSession session, Model model) {
		if(session.getAttribute("admin")==null) {
			return "redirect:/adminlogin";
		}
		List<Enquiry> enq=erepo.findAll();
		model.addAttribute("enq", enq);
		return "admin/enquiries";
		
		// change password codes===================================================
		
	}
	@GetMapping("/admin/changeadminpwd")
	public String changeAdminPassword(HttpSession session) {
		if(session.getAttribute("admin")==null) {
			return "redirect:/adminlogin";
		}
		return "admin/changeadminpwd";
	}
	@PostMapping("/admin/changeadminpwd")
	public String changeAdminPwd(HttpSession session, HttpServletRequest  request, RedirectAttributes attrib) {
		if(session.getAttribute("admin")==null) {
			return "rediret:/adminlogin";
		}
		String oldpassword=request.getParameter("oldpassword");
		String newpassword=request.getParameter("newpassword");
		String confirmpassword=request.getParameter("confirmpassword");
		if(!newpassword.equals(confirmpassword)) {
			attrib.addFlashAttribute("msg","Newpassword and confirmpassword are not matched");
			return "redirect:/admin/changeadminpwd";
		}
		try {
			AdminInfo admin =(AdminInfo) session.getAttribute("admin");
			if(!admin.getPassword().equals(oldpassword)) {
				attrib.addFlashAttribute("msg","oldpassword id not matched");
				return "redirect:/admin/changeadminpwd";
			}
			admin.setPassword(newpassword);
			airepo.save(admin);
			return "redirect:/admin/logout";
		}
		catch(Exception e) {
			attrib.addFlashAttribute("msg","AdminId not matched");
			return "redirect:/admin/changeadminpwd";
		}
	}
	@GetMapping("/admin/viewfeedback")
	public String viewFeedback(Model model, HttpSession session) {
		if(session.getAttribute("admin")==null) {
			return "rediret:/adminlogin";
		}
		List<Response> feed=rrepo.findByResponsetype("feedback");
		model.addAttribute("feed", feed);
		return "admin/viewfeedback";
	}
	@GetMapping("/admin/viewcomplaint")
	public String viewComplaint(Model model, HttpSession session) {
		if(session.getAttribute("admin")==null) {
			return "redirect:/adminlogin";
		}
		List<Response> comp=rrepo.findByResponsetype("complaint");
		model.addAttribute("comp", comp);
		return "admin/viewcomplaint";
	}
	@GetMapping("/admin/deleteenq")
	public String deleteEnquiry(HttpSession session, @RequestParam int id, RedirectAttributes attrib) {
		if(session.getAttribute("admin")==null) {
			return "redircet:/adminlogin";
		}
		Enquiry e=erepo.findById(id).get();
		erepo.delete(e);
		attrib.addFlashAttribute("msg", "Enquiry is deleted successffully");
		return "redirect:/admin/enquiries";
	}
	@GetMapping("/admin/deletecomplaint")
	public String deleteComplaint(HttpSession session, @RequestParam int id, RedirectAttributes attrib) {
		if(session.getAttribute("admin")==null) {
			return "rediret:/adminlogin";
		}
		Response r=rrepo.findById(id).get();
		rrepo.delete(r);
		attrib.addFlashAttribute("msg", "Complaint is deleted successffully");
		return "redirect:/admin/viewcomplaint";
	}
	
	
	/*
	 * @GetMapping("/admin/deletejob") public String deleteJobs(Model
	 * model, @RequestParam int id,HttpSession session ,RedirectAttributes attrib) {
	 * if(session.getAttribute("admin")==null) { return "redirect:/adminlogin"; }
	 * 
	 * JobInfo ji=jrepo.findById(id).get(); jrepo.delete(ji);
	 * attrib.addFlashAttribute("msg", "Posted job delete Successfully."); return
	 * "redirect:/admin/postedJobs"; }
	 */
	
	@GetMapping("/admin/deletejob")
	public String deleteJobs(Model model, @RequestParam int id, HttpSession session, RedirectAttributes attrib) {
	    if (session.getAttribute("admin") == null) {
	        return "redirect:/adminlogin";
	    }

	    JobInfo ji = jrepo.findById(id).get();
	    jrepo.delete(ji);
	    attrib.addFlashAttribute("msg", "Posted job delete Successfully.");

	    // YAHAN BADLAV KAREIN: /admin/postedJobs ko hata kar /admin/viewjob kijiye
	    return "redirect:/admin/viewjob";
	}
	
	
	@GetMapping("/admin/appliedjobs")
	public String viewAppliedJobs(HttpSession session, Model model) {
		if(session.getAttribute("admin")==null) {
		return "redirect:/adminlogin";
		}
		List<AppliedJob> aj=ajrepo.findAll();
		model.addAttribute("aj" , aj);		
		return "admin/appliedjobs";
	}
}





