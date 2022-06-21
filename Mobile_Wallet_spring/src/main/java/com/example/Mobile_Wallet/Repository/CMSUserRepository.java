package com.example.Mobile_Wallet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Mobile_Wallet.Database.CMS_User;

public interface CMSUserRepository extends JpaRepository<CMS_User,Integer>{
	
	@Query(value="select * from cms.user where mobileno=?1",nativeQuery=true)
	public CMS_User getByMobile(String mobileno);
	
	@Query(value="select userid from cms.user where mobileno=?1",nativeQuery=true)
	public String get_user_id_from_mobile(String mobileno);
	
	@Query(value="select userid from cms.user where email=?1",nativeQuery=true)
	public String get_user_id_from_email(String email);
	
	@Query(value="select count(*) from cms.user where email=?1",nativeQuery=true)
	public int countByEmail(String email);
	
	@Query(value="select count(*) from cms.user where mobileno=?1",nativeQuery=true)
	public int countBymobileno(String mobileno);
}
