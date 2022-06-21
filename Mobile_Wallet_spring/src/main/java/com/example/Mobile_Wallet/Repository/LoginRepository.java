package com.example.Mobile_Wallet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.Login;

public interface LoginRepository extends JpaRepository<Login, Integer> {
	
	@Query(value="select * from WMS.login where Username=?1",nativeQuery=true)
	public Login getByUsername(String username);
	
	@Query(value="select ifnull(max(loginid),0) from WMS.login",nativeQuery=true)
	public Integer getMaxLoginId();
	
	@Query(value="select * from WMS.login,wms.user where user.userid=login.userid and mobileno=?1",nativeQuery=true)
	public Login getByMobileNo(String mobileno);
	
	@Modifying
	@Transactional
	@Query(value="insert into WMS.login values (?1,?2,?3,?4,now(),now(),'ACTIVE')",nativeQuery = true)
	public void insert(int loginid,int userid,String username,String password);
	
	@Modifying
	@Transactional
	@Query(value="update WMS.login set password=?2 where username=?1",nativeQuery = true)
	public void updatePassword(String username,String password);
	
	@Modifying
	@Transactional
	@Query(value="update wms.login set username=?3,password=?4 where userid=?2 and loginid=?1",nativeQuery=true)
	public void add(Integer loginid, int userid, String username, String password);

	@Query(value="select loginid from wms.login where userid=?1",nativeQuery=true)
	public int getIdByUserId(int userid);
	
}