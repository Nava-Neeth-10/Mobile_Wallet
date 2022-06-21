package com.example.Mobile_Wallet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Query(value="select * from WMS.user where mobileno=?1",nativeQuery = true)
	public User getByMobileNo(String mobileno);
	
	@Query(value="select * from WMS.user where userid=?1",nativeQuery = true)
	public User getById(int userid);
	
	@Query(value="select * from WMS.user,wms.login where user.userid=login.userid and username=?1",nativeQuery = true)
	public User getByUsername(String username);
	
	@Query(value="select * from WMS.user,wms.userdetails where user.userid=userdetails.userid and email=?1",nativeQuery = true)
	public User getByEmail(String email);
	
	@Query(value="select ifnull(max(userid),0) from WMS.user",nativeQuery=true)
	public Integer getMaxUserId();
	
	@Query(value="select primarywallet from WMS.user where mobileno=?1",nativeQuery=true)
	public String getPWId(String mobileno);
	
	@Modifying
	@Transactional
	@Query(value="update WMS.user set primarywallet=?1 where mobileno=?2",nativeQuery=true)
	void setPWId(String pwallet, String mobileno);
	
	@Modifying
	@Transactional
	@Query(value="insert into WMS.user values (?1,?2,?3,null,?4,now(),null)",nativeQuery = true)
	void insert(Integer id,String usertype,String mobileno,String userstatus);
	
	@Modifying
	@Transactional
	@Query(value="update WMS.user set otpid=?2 where mobileno=?1",nativeQuery = true)
	void updateOtpId(String mobileno,Integer otpid);
	
	@Modifying
	@Transactional
	@Query(value="update WMS.user set userstatus=?2 where mobileno=?1",nativeQuery = true)
	void updateStatus(String mobileno,String status);

	@Modifying
	@Transactional
	@Query(value="update wms.user set usertype=?2,userstatus=?4 where userid=?1 and mobileno=?3",nativeQuery=true)
	public void add(int userid, String type, String mobileno, String status);

	
}