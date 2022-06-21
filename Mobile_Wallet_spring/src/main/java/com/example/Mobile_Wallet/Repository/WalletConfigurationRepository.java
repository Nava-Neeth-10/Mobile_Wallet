package com.example.Mobile_Wallet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.WalletConfiguration;
import com.example.Mobile_Wallet.Database.WalletConfigurationId;

public interface WalletConfigurationRepository extends JpaRepository<WalletConfiguration,WalletConfigurationId> {
	
	@Modifying
	@Transactional
	@Query(value="Insert into WMS.walletconfiguration values (?1,?2,?3,?4,?5,?6,?7,?8,10000,'','CLOSED')",nativeQuery=true)
	public void insert(String walletid,String pin,String paymenttype,Double transactionlimitsingle,Integer transactioncountlimitdaily,Double transactionlimitdaily,Double transactionlimitmonthly,Double transactionlimityearly,Double balance);

	@Modifying
	@Transactional
	@Query(value="update WMS.walletconfiguration set region=if(region='',?2,concat(region,',',?2)) where walletid=?1",nativeQuery=true)
	public void updateRegion(String walletid,String region);
	
	@Query(value="select region from WMS.walletconfiguration where walletid=?1 limit 1",nativeQuery=true)
	public String getRegionById(String walletid);
	
	@Query(value="select * from WMS.walletconfiguration where walletid=?1 and pin=?2 and paymenttype=?3",nativeQuery=true)
	public WalletConfiguration getById(String walletid,String pin,String type);
	
}
