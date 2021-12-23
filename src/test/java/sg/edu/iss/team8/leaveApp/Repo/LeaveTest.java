package sg.edu.iss.team8.leaveApp.Repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import sg.edu.iss.team8.leaveApp.Team8LeaveApplication;
import sg.edu.iss.team8.leaveApp.helpers.LeaveEnum;
import sg.edu.iss.team8.leaveApp.helpers.StatusEnum;
import sg.edu.iss.team8.leaveApp.model.Leave;
import sg.edu.iss.team8.leaveApp.repo.LeaveRepo;
import sg.edu.iss.team8.leaveApp.service.LeaveService;
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Team8LeaveApplication.class)
@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureTestDatabase(replace =
AutoConfigureTestDatabase.Replace.NONE)

public class LeaveTest {

	@Autowired
	private LeaveRepo lrepo;
  
  @Autowired
	private LeaveService lservice;
	
  	
	@Test
	@Order(1)
	public void testCreateLeave() throws ParseException {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Leave leave1 = new Leave(LocalDate.parse("2020-01-08", format), LocalDate.parse("2020-01-10", format), LeaveEnum.ANNUAL , "...",
				"...", "91111", StatusEnum.APPLIED, "..."); 
		Leave leave2 = new Leave(LocalDate.parse("2020-01-08", format), LocalDate.parse("2020-01-10", format), LeaveEnum.COMPENSATION , "...",
				"...", "91111", StatusEnum.DELETED, "..."); 
		Leave leave3 = new Leave(LocalDate.parse("2020-01-08", format), LocalDate.parse("2020-01-10", format) ,LeaveEnum.MEDICAL , "...",
				"...", "91111", StatusEnum.APPROVED, "...");
		lrepo.deleteAll();
		lrepo.saveAndFlush(leave1);
		lrepo.saveAndFlush(leave2);
		lrepo.saveAndFlush(leave3);
		List<Leave> leaves = lrepo.findAll();
		assertEquals(leaves.size(), 3);
	}

	
	@Test
	@Order(2)
	public void testFindLeaveByLeaveId() {
		List<Leave> leaves = lrepo.findAll();
		Integer id1 = leaves.get(0).getLeaveId();
		Integer id2 = leaves.get(1).getLeaveId();
		Leave sampleLeave1 = lrepo.findLeaveById(id1);
		Leave sampleLeave2 = lrepo.findLeaveById(id2);
		List<Leave> leavesList = new ArrayList<>();
		leavesList.add(sampleLeave1);
		leavesList.add(sampleLeave2);
		assertEquals(leavesList.size(), 2);
	}
	
	@Test
	@Order(3)
	public void testDateDifference() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date1 = LocalDate.parse("2021-12-10", format);
		LocalDate date2 = LocalDate.parse("2021-12-15", format);
		Period period = Period.between(date1, date2);
		int diff = Math.abs(period.getDays());
		assertEquals(5, diff);
	}
	

	@Test
	@Order(4)
	public void tesLeaveExclusionCalculation() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Leave leave = new Leave(LocalDate.parse("2021-12-15", format), LocalDate.parse("2021-12-22", format), LeaveEnum.ANNUAL , "...",
				"...", "91111", StatusEnum.APPLIED, "...");
		int result = lservice.calculateDaysToExclude(leave);
		assertEquals(result, 2);

	}
	
	@Test
	@Order(5)
	public void testLeaveExclusionCalculation2() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Leave leave = new Leave(LocalDate.parse("2021-12-15", format), LocalDate.parse("2021-12-30", format), LeaveEnum.ANNUAL , "...",
				"...", "91111", StatusEnum.APPLIED, "...");
		int result = lservice.calculateDaysToExclude(leave);
		assertEquals(result, 0);	//should be 0 because > 14 days

	}
	
	@Test
	@Order(6)
	public void testLeaveExclusionCalculation3() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Leave leave = new Leave(LocalDate.parse("2021-12-15", format), LocalDate.parse("2021-12-22", format), LeaveEnum.MEDICAL , "...",
				"...", "91111", StatusEnum.APPLIED, "...");
		int result = lservice.calculateDaysToExclude(leave);
		assertEquals(result, 0);	//should be 0 because MEDICAL Leave

	}

	@Test
	@Order(7)
	public void testLeaveExclusionCalculation4() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Leave leave = new Leave(LocalDate.parse("2021-11-03", format), LocalDate.parse("2021-11-05", format), LeaveEnum.ANNUAL , "...",
				"...", "91111", StatusEnum.APPLIED, "...");
		int result = lservice.calculateDaysToExclude(leave);
		assertEquals(result, 1);	//should exclude 1 because 4 Nov is PH.
	}
	
	@Test
	@Order(8)
	public void testUpdateLeave() {
		List<Leave> leaves = lrepo.findAll();
		Integer id1 = leaves.get(0).getLeaveId();
		Leave sampleLeave1 = lrepo.findLeaveById(id1);
		lservice.updateLeave(sampleLeave1);
		Leave sampleLeaveUpdated = lrepo.findLeaveById(id1);
		StatusEnum result = sampleLeaveUpdated.getStatus();
		assertEquals(result, StatusEnum.UPDATED);
		
	}
	
	/*
	@Test
	public void testUpdateLeave()
	{
		
		System.out.println("Executing testUpdateLeave()");
		Integer leaveId = 1;
		Leave currentLeave = lservice.findLeaveById(leaveId);
		String reason = "daniel";
		currentLeave.setAddtnlReason(reason);
		System.out.println(currentLeave.getStatus());
		System.out.println(currentLeave.getAddtnlReason());

		lservice.updateLeave(currentLeave);
		System.out.println("Leave updated");
		currentLeave = lservice.findLeaveById(leaveId);
		assertEquals(currentLeave.getAddtnlReason(),reason);
		
		
		
	}
	
	@Test
    public void testGetAllLeaves()
    {
		List<Leave> leaves = lrepo.findAll();
		
		for(Leave leave : leaves)
		{
			System.out.println(leave.getLeaveId());
		}
		assertEquals(leaves.size(),5);
    }
    */
	
}
