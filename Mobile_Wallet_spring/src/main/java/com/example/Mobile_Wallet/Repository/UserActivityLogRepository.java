package com.example.Mobile_Wallet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.UserActivityLog;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Integer> {
	
	@Modifying
	@Transactional
	@Query(value="insert into WMS.UserActivityLog (userid,walletid,datapoints,timestamp) values (?1,?2,?3,now())",nativeQuery = true)
	void insert(int userid,String walletid,String json);

}
