package com.example.Mobile_Wallet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {
	
	@Query(value="select * from WMS.userdetails,wms.user where user.userid=userdetails.userid and mobileno=?1",nativeQuery=true)
	public UserDetails getByMobileNo(String mobileno);
	
	@Query(value="select * from WMS.userdetails where email=?1",nativeQuery=true)
	public UserDetails getByEmail(String email);
	
	@Query(value="select * from WMS.userdetails,wms.login where login.userid=userdetails.userid and username=?1",nativeQuery=true)
	public UserDetails getByUsername(String username);
	
	@Query(value="select ifnull(max(userdetailsid),0) from WMS.UserDetails",nativeQuery=true)
	public Integer getMaxUserDetailsId();
	
	@Modifying
	@Transactional
	@Query(value="insert into WMS.userdetails values (?1,?2,?3,?4,?5,?6,?7)",nativeQuery = true)
	public void insert(int id,String firstname,String middlename,String lastname,String email,String countrycode,String dob);
	
	@Modifying
	@Transactional
	@Query(value="update WMS.userdetails set firstname=?2,middlename=?3,lastname=?4,dob=?5 where userid=?1",nativeQuery = true)
	public void update(int id,String firstname,String middlename,String lastname,String dob);

	@Modifying
	@Transactional
	@Query(value="update WMS.userdetails set firstname=?2,middlename=?3,lastname=?4,email=?5,countrycode=?6,dob=?7 where userid=?1",nativeQuery=true)
	public void add(int userid, String first_name, String middle_name, String last_name, String email,String countrycode, String dob);

}
