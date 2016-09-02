CREATE TABLE users (
   id char(36) not null primary key,
   created TIMESTAMP default current_timestamp not null,
   lastModified TIMESTAMP null,
   version BIGINT default 0 not null,
   username VARCHAR(255) not null,
   password VARCHAR(255) not null,
   email VARCHAR(255) not null,
   authorities VARCHAR(1024) default 'uaa.user' not null,
   givenName VARCHAR(255),
   familyName VARCHAR(255),
   active BOOLEAN default true not null,
   phoneNumber VARCHAR(255),
   verified BOOLEAN default false not null,
   origin varchar(36) default 'uaa' NOT NULL,
   external_id varchar(255) default NULL,
   identity_zone_id varchar(36) DEFAULT 'uaa',
   salt VARCHAR(36) default NULL,
   passwd_lastmodified TIMESTAMP NULL,
   legacy_verification_behavior BOOLEAN DEFAULT FALSE NOT NULL
) ;

create table UserConnection (userId varchar(255) not null,
	providerId varchar(255) not null,
	providerUserId varchar(255),
	rank int not null,
	displayName varchar(255),
	profileUrl varchar(512),
	imageUrl varchar(512),
	accessToken varchar(512) not null,
	secret varchar(512),
	refreshToken varchar(512),
	expireTime bigint,
	primary key (userId, providerId, providerUserId));
create unique index UserConnectionRank on UserConnection(userId, providerId, rank);

CREATE TABLE groups (
  id VARCHAR(36) not null primary key,
  displayName VARCHAR(255) not null,
  created TIMESTAMP default current_timestamp not null,
  lastModified TIMESTAMP null,
  version INTEGER default 0 not null,
  identity_zone_id varchar(36) DEFAULT 'uaa' NOT NULL,
  description varchar(255),
  constraint unique_uk_2 unique(displayName)
) ;

CREATE TABLE group_membership (
  group_id VARCHAR(36) not null,
  member_id VARCHAR(36) not null,
  member_type VARCHAR(8) default 'USER' not null,
  authorities VARCHAR(255) default 'READ' not null,
  added TIMESTAMP default current_timestamp not null,
  origin varchar(36) default 'uaa' NOT NULL,
  identity_zone_id varchar(36) DEFAULT 'uaa',
  primary key (group_id, member_id)
) ;

CREATE TABLE sec_audit (
   principal_id VARCHAR(255) not null,
   event_type INTEGER not null,
   origin VARCHAR(255) not null,
   event_data VARCHAR(255),
   created TIMESTAMP default current_timestamp,
   identity_zone_id varchar(36) DEFAULT 'uaa'
) ;