package com.example.Mobile_Wallet.Repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.Mobile_Wallet.Database.Transactions;

public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {
	
	@Modifying
	@Transactional
	@Query(value="INSERT INTO WMS.transactions (transactionid,senderuserid,senderwalletid,receiveruserid,receiverwalletid,transactionamount,transactiontype,senderdeviceid,responsecode,transactiontime) VALUES (?1,?2,?3,?4,?5,?6,?7,?8,?9,now());",nativeQuery = true)
	void insert(String transactionid,Integer senderuserid,String senderwalletid,Integer receiveruserid,String receiverwalletid,Double transactionamount,String transactiontype,String deviceid,String responsecode);
	
	@Query(value="select transactionid from WMS.transactions where transactionid like ?1%",nativeQuery=true)
	public List<String> getid(String search);

	@Query(value="select ifnull(sum(transactionamount),0) from WMS.transactions where senderwalletid=?1 and responsecode='200' and transactiontime between date_add(now(),interval -1 day) and now()",nativeQuery=true)
	public int getDailySender(String senderwalletid);

	@Query(value="select ifnull(count(transactionamount),0) from WMS.transactions where senderwalletid=?1 and responsecode='200' and transactiontime between date_add(now(),interval -1 day) and now()",nativeQuery=true)
	public int getDailyCountSender(String senderwalletid);

	@Query(value="select ifnull(sum(transactionamount),0) from WMS.transactions where senderwalletid=?1 and responsecode='200' and transactiontime between date_add(now(),interval -1 month) and now()",nativeQuery=true)
	public Integer getMonthlySender(String senderwalletid);
	
	@Query(value="select ifnull(sum(transactionamount),0) from WMS.transactions where senderwalletid=?1 and responsecode='200' and transactiontime between date_add(now(),interval -1 year) and now()",nativeQuery=true)
	public int getYearlySender(String senderwalletid);
	
	@Query(value="select ifnull(sum(transactionamount),0) from WMS.transactions where receiverwalletid=?1 and responsecode='200' and transactiontime between date_add(now(),interval -1 day) and now()",nativeQuery=true)
	public int getDailyReceiver(String receiverwalletid);

	@Query(value="select ifnull(count(transactionamount),0) from WMS.transactions where receiverwalletid=?1 and responsecode='200' and transactiontime between date_add(now(),interval -1 day) and now()",nativeQuery=true)
	public int getDailyCountReceiver(String receiverwalletid);

	@Query(value="select ifnull(sum(transactionamount),0) from WMS.transactions where receiverwalletid=?1 and responsecode='200' and transactiontime between date_add(now(),interval -1 month) and now()",nativeQuery=true)
	public Integer getMonthlyReceiver(String receiverwalletid);
	
	@Query(value="select ifnull(sum(transactionamount),0) from WMS.transactions where receiverwalletid=?1 and responsecode='200' and transactiontime between date_add(now(),interval -1 year) and now()",nativeQuery=true)
	public int getYearlyReceiver(String receiverwalletid);
	
	@Query(value="select transactionid from wms.transactions where senderwalletid=?1 && transactiontime in(select max(transactiontime) from wms.transactions)",nativeQuery=true)
	public String gettId(String walletid);
	
	@Query(value="select * from WMS.transactions where receiverwalletid in :wallets or senderwalletid in :wallets order by transactiontime",nativeQuery=true)
	public ArrayList<Transactions> getUserTransactions(@Param("wallets") ArrayList<String> wallets);
	
	/*@Query(value="select transactionamount from wms.transactions where senderwalletid=?1 && where transactiontime in(select max(transactiontime) from wms.transaction)")
	public String getamount(String walletid);*/

}
