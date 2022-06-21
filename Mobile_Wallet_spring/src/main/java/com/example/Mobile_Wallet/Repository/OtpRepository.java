package com.example.Mobile_Wallet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.Otp;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
	
	@Modifying
	@Transactional
	@Query(value="insert into WMS.Otp values (?1,?2,now(),?3,?4,?5)",nativeQuery = true)
	void insert(int id,int otp,String reason,String key,String attempts);
	
	@Modifying
	@Transactional
	@Query(value="delete from WMS.Otp where otpid=?1",nativeQuery = true)
	void deleteByOtp(int id);
	
	@Query(value="select ifnull(max(otpid),0) from WMS.Otp",nativeQuery=true)
	public Integer getMaxOtpId();
	
	@Query(value="select * from wms.otp where otpid=?1 and date_add(createdon,interval ?2 second)>now()",nativeQuery=true)
	public Otp checkExpiry(int id,String expiry);

	@Query(value="select * from WMS.otp,wms.user where user.otpid=otp.otpid and mobileno=?1",nativeQuery=true)
	public Otp getOtpByMobileNo(String mobileno);
	
	@Modifying
	@Transactional
	@Query(value="update WMS.otp set attempts=?2 where otpid=?1",nativeQuery = true)
	void updateAttempts(int id,int attempts);

}
