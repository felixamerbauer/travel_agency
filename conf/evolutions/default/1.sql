# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `event` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`title` VARCHAR(254) NOT NULL,`description` VARCHAR(254) NOT NULL,`location` VARCHAR(254) NOT NULL,`start` TIMESTAMP NOT NULL,`end` TIMESTAMP NULL);
create table `game` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`opponent` VARCHAR(254) NOT NULL,`start` TIMESTAMP NOT NULL,`end` TIMESTAMP NOT NULL,`location` VARCHAR(254) NOT NULL,`homematch` BOOLEAN NOT NULL,`comment` VARCHAR(254) NOT NULL,`membershipId` INTEGER NOT NULL,`result` VARCHAR(254) NOT NULL,`laundererId` BIGINT);
create table `membership` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` INTEGER NOT NULL,`fullname` VARCHAR(254) NOT NULL);
create table `news` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`title` VARCHAR(254) NOT NULL,`content` VARCHAR(254) NOT NULL,`published` TIMESTAMP NOT NULL,`teaser` VARCHAR(254) NOT NULL,`photo` VARCHAR(254));
create table `news_membership` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`newsId` BIGINT NOT NULL,`membershipId` BIGINT NOT NULL);
create table `parent` (`personId` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`tmp` VARCHAR(254));
create table `person` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`firstname` VARCHAR(254) NOT NULL,`lastname` VARCHAR(254) NOT NULL,`email` VARCHAR(254) NOT NULL,`password` VARCHAR(254) NOT NULL,`superuser` BOOLEAN NOT NULL,`birthday` TIMESTAMP NULL,`phone` VARCHAR(254));
create table `player` (`personId` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`position` VARCHAR(254),`alias` VARCHAR(254),`joining` TIMESTAMP NULL,`trivia` VARCHAR(254),`hofgames` INTEGER NOT NULL,`hofgoals` INTEGER NOT NULL,`active` BOOLEAN NOT NULL);
create table `user_game` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`userId` INTEGER NOT NULL,`gameId` INTEGER NOT NULL,`signedIn` INTEGER NOT NULL,`comment` VARCHAR(254));
create table `user_membership` (`id` INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,`userId` INTEGER NOT NULL,`membershipId` INTEGER NOT NULL);

# --- !Downs

drop table `event`;
drop table `game`;
drop table `membership`;
drop table `news`;
drop table `news_membership`;
drop table `parent`;
drop table `person`;
drop table `player`;
drop table `user_game`;
drop table `user_membership`;

