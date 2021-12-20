package sg.edu.iss.team8.leaveApp.repo;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sg.edu.iss.team8.leaveApp.model.Employee;
import sg.edu.iss.team8.leaveApp.model.Leave;
import sg.edu.iss.team8.leaveApp.model.Manager;
import sg.edu.iss.team8.leaveApp.model.User;

public interface UserRepo extends JpaRepository<User, Integer> {

	@Transactional
	@Modifying
	@Query(value = "update User U set U.user_type = :usertype where u.name = :name", nativeQuery = true)
	public void updateUserType(@Param("usertype") String usertype, @Param("name") String name);

	@Query("select U from User U where TYPE(U) = 'employee'")
	public List<Employee> getAllEmployees();

	@Query("select U from User U where TYPE(U) = 'manager'")
	public List<Manager> getAllManagers();

	@Query("SELECT DISTINCT u2 FROM User u1, User u2 WHERE u1.userId = u2.reportsTo AND u1.userId = :userId")
	public List<Employee> findSubordinates(@Param("userId") Integer userId);
	
	@Query("SELECT U.leaves FROM User U WHERE U.userId = :userId")
	public List<Leave> findLeaveByUID(@Param("userId")Integer userId); 
	
	public Employee findByUserId(Integer userId); 
	
//	@Query("SELECT L FROM User U JOIN U.leaves L WHERE U.userid = :userId  AND U.leaves.status = StatusEnum.APPLIED")
//	public List<Leave> findPendingLeaveByUID(@Param("userId") Integer userId);
	
}
