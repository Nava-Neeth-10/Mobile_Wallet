drop database if exists WMS;
create database WMS;
use WMS;

CREATE TABLE User (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    UserType enum('CUST','BUS') NOT NULL,
    MobileNo CHAR(10) NOT NULL UNIQUE,
    OtpID INT,
    UserStatus enum('ACTIVE','INACTIVE') NOT NULL,
    CreatedDate DATETIME NOT NULL,
    primarywallet CHAR(14) NULL
);

CREATE TABLE UserDetails (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    FirstName CHAR(15),
    MiddleName CHAR(15),
    LastName CHAR(15),
    Email CHAR(50),
    CountryCode INT,
    DoB DATE
);

CREATE TABLE Login (
    LoginID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    UserName CHAR(30) NOT NULL UNIQUE,
    Password CHAR(30) NOT NULL,
    CreatedDate DATETIME NOT NULL,
    LastUpdatedDate DATETIME NOT NULL,
    LoginStatus enum('ACTIVE','INACTIVE') NOT NULL
);

CREATE TABLE  Wallet (
    WalletID CHAR(14) PRIMARY KEY,
    UserID INT NOT NULL,
    walletname char(10),
    AccountType enum('BUSINESS','STANDARD','PARTNER') NOT NULL,
    WalletType enum('EMONEY','FEES') NOT NULL,
    Balance DECIMAL,
    Currency CHAR(3),
    Status enum('ACTIVE','INACTIVE') NOT NULL,
    LastUpdatedDate DATETIME NOT NULL
);

CREATE TABLE WalletConfiguration (
    WalletID CHAR(14),
    PIN enum('Y','N'),
    PaymentType enum('UPI','IMPS','NEFT','RTGS','WALLET'),
    TransactionLimitSingle DECIMAL,
    TransactionCountLimitDaily INT,
    TransactionLimitDaily DECIMAL,
    TransactionLimitMonthly DECIMAL,
    TransactionLimitYearly DECIMAL,
    BalanceLimit DECIMAL NOT NULL,
    Region VARCHAR(300) NOT NULL,
    WalletType enum('OPEN','SEMI','CLOSED') NOT NULL,
    PRIMARY KEY(WalletID,PIN,PaymentType)
);

CREATE TABLE OTP (
    OtpID INT PRIMARY KEY,
    Otp INT NOT NULL,
    CreatedOn DATETIME NOT NULL,
    Reason enum('REGISTER','LOGIN','PASSWORD','WALLET') NOT NULL,
    OtpKey CHAR(30),
    Attempts INT NOT NULL
);
Create table WMS_Token(
	TokenNumber CHAR(20) Primary key,
	WalletID CHAR(14) NOT NULL,
    CardNumber CHAR(4) NOT NULL,
    CardIssuer CHAR(10) NOT NULL,
    TokenExpiry DATETIME NOT NULL,
    Status enum('ACTIVE','INACTIVE') NOT NULL
);

Create table UserActivityLog(
	UserActivityLogID Int AUTO_INCREMENT PRIMARY KEY,
	UserId INT NOT NULL,
	WalletID CHAR(14),
	DataPoints JSON ,
    TimeStamp DATETIME,
	FOREIGN KEY  (UserId) REFERENCES User(UserID)
);

CREATE TABLE transactions (
    transactionid varchar(25) PRIMARY KEY,
    senderuserid int,
    senderwalletid char(14),
    receiveruserid int,
    receiverwalletid char(14),
    transactionamount decimal,
    transactiontype varchar(3),
    senderdeviceid varchar(255),
    responsecode varchar(255),
    transactiontime datetime
);

insert into wms.wms_token (tokennumber,walletid,cardnumber,cardissuer,tokenexpiry,status) values ('10102020303040405050','00000000000001','4444','HDFC',date_add(now(),interval '1' day_hour),'ACTIVE');
insert into wms.wms_token (tokennumber,walletid,cardnumber,cardissuer,tokenexpiry,status) values ('12345678900987654321','00000000000001','3333','SBI',date_add(now(),interval '1' day_hour),'ACTIVE');
drop database if exists CMS;
create database CMS;
use CMS;

CREATE TABLE User(
    UserID int NOT NULL,
    UserType enum('CUSTOMER','BUSINESS') NOT NULL,
    MobileNo VARCHAR(10) NOT NULL,
    Email VARCHAR(50) NOT NULL,
    WebLogin VARCHAR(30),
    MobileLogin VARCHAR(30),
    FirstName VARCHAR(15),
    MiddleName VARCHAR(15),
    LastName VARCHAR(15),
    DOB DATE,
    AadharNo VARCHAR(12),
    PANno VARCHAR(10),
    PRIMARY KEY(UserID)
);

CREATE TABLE Account(
    AccountID int PRIMARY KEY,
    UserID int NOT NULL REFERENCES User(UserID),
    CardNumber CHAR(16) NOT NULL,
    CVV INT(3) NOT NULL,
    Expiry DATE NOT NULL,
    AccountType enum('CURRENT','SAVINGS'),
    Balance DECIMAL NOT NULL,
    Status enum('ACTIVE','INACTIVE')
);

CREATE TABLE CMS_Token(
    TokenID int primary key auto_increment,
    TokenNumber CHAR(20) NOT NULL,
    AccountId INT NOT NULL,
    WalletId CHAR(14) NOT NULL,
    TokenExpiry DATETIME NOT NULL,
    Status enum('ACTIVE','INACTIVE') NOT NULL,
    CreatedDate DATETIME NOT NULL
);

INSERT INTO User values(1,'CUSTOMER','8939090098','pindikantivivek@gmail.com',NULL,NULL,'vivek',NULL,NULL,NULL,NULL,NULL);
INSERT INTO ACCOUNT VALUES(1,1,'1111222233334444','123','2030-12-01','CURRENT',120000,'ACTIVE');
INSERT INTO ACCOUNT VALUES(2,1,'1111222233334455','123','2030-12-01','CURRENT',460,'INACTIVE');
INSERT INTO ACCOUNT VALUES(3,1,'1111222233334466','123','2030-12-01','CURRENT',20000,'ACTIVE');
