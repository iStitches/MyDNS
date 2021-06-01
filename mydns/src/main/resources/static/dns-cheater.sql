/*
Navicat MySQL Data Transfer

Source Server         : LocalMysql
Source Server Version : 80022
Source Host           : localhost:3306
Source Database       : dns-cheater

Target Server Type    : MYSQL
Target Server Version : 80022
File Encoding         : 65001

Date: 2021-05-31 22:49:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ruleId` bigint DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL COMMENT '地址类型，IPv4或IPv6',
  `address` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `address_ruleid_idx` (`ruleId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='规则所设定的IP集';

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES ('4', '3', 'IPv4', '192.168.10.2');
INSERT INTO `address` VALUES ('5', '3', 'IPv4', '192.168.10.3');
INSERT INTO `address` VALUES ('6', '3', 'IPv4', '192.168.10.4');
INSERT INTO `address` VALUES ('7', '3', 'IPv4', '192.168.10.5');
INSERT INTO `address` VALUES ('8', '4', 'IPv4', '127.0.0.1');
INSERT INTO `address` VALUES ('9', '4', 'IPv4', '127.0.0.2');
INSERT INTO `address` VALUES ('10', '4', 'IPv4', '127.0.0.3');
INSERT INTO `address` VALUES ('11', '4', 'IPv4', '127.0.0.4');
INSERT INTO `address` VALUES ('12', '3', 'IPv4', '192.168.10.6');
INSERT INTO `address` VALUES ('13', '3', 'IPv4', '192.168.10.7');
INSERT INTO `address` VALUES ('14', '3', 'IPv4', '192.168.10.8');
INSERT INTO `address` VALUES ('15', '3', 'IPv4', '192.168.10.9');
INSERT INTO `address` VALUES ('16', '3', 'IPv4', '192.168.10.10');
INSERT INTO `address` VALUES ('20', '8', 'IPv4', '127.0.0.1');
INSERT INTO `address` VALUES ('30', '10', 'IPv4', '192.168.22.1');
INSERT INTO `address` VALUES ('31', '10', 'IPv4', '192.168.22.2');
INSERT INTO `address` VALUES ('32', '10', 'IPv4', '192.168.22.3');
INSERT INTO `address` VALUES ('33', '10', 'IPv4', '192.168.22.4');
INSERT INTO `address` VALUES ('34', '18', 'ipv4', '192.222.111.5');
INSERT INTO `address` VALUES ('35', '18', 'ipv4', '192.222.111.6');
INSERT INTO `address` VALUES ('36', '18', 'ipv4', '192.222.111.7');
INSERT INTO `address` VALUES ('37', '18', 'ipv4', '192.222.111.8');
INSERT INTO `address` VALUES ('38', '18', 'ipv4', '192.222.111.9');
INSERT INTO `address` VALUES ('44', '24', 'ipv4', '192.333.666.1');
INSERT INTO `address` VALUES ('45', '24', 'ipv4', '192.333.666.2');
INSERT INTO `address` VALUES ('46', '24', 'ipv4', '192.333.666.3');
INSERT INTO `address` VALUES ('47', '24', 'ipv4', '192.333.666.4');
INSERT INTO `address` VALUES ('48', '24', 'ipv4', '192.333.666.5');
INSERT INTO `address` VALUES ('49', '25', 'ipv4', '193.223.235.1');
INSERT INTO `address` VALUES ('50', '25', 'ipv4', '193.223.235.2');
INSERT INTO `address` VALUES ('51', '25', 'ipv4', '193.223.235.3');
INSERT INTO `address` VALUES ('52', '25', 'ipv4', '193.223.235.4');
INSERT INTO `address` VALUES ('53', '25', 'ipv4', '193.223.235.5');

-- ----------------------------
-- Table structure for rule
-- ----------------------------
DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ipFrom` bigint DEFAULT NULL COMMENT 'IP范围开始地址',
  `ipTo` bigint DEFAULT NULL COMMENT 'IP范围结束地址',
  `timeFrom` int DEFAULT NULL COMMENT '时间段开始时间，格式：HHmmss',
  `timeTo` int DEFAULT NULL COMMENT '时间段结束时间',
  `matchMode` varchar(20) DEFAULT NULL COMMENT '域名匹配模式',
  `name` varchar(100) DEFAULT NULL COMMENT '匹配的域名',
  `priority` int DEFAULT NULL COMMENT '匹配优先级',
  `enabled` bit(1) DEFAULT NULL COMMENT '是否启用',
  `dispatchMode` varchar(20) DEFAULT NULL COMMENT '分发模式，如iphash、round-robin、random',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci COMMENT='解析规则';

-- ----------------------------
-- Records of rule
-- ----------------------------
INSERT INTO `rule` VALUES ('3', '16777217', '3758096382', '0', '235959', 'suffix', '.wukon.com.cn', '0', '', 'hash');
INSERT INTO `rule` VALUES ('4', '16777217', '3758096382', '0', '235959', 'contains', '.weibo.', '0', '', 'hash');
INSERT INTO `rule` VALUES ('8', '16777217', '3758096382', '0', '235900', 'suffix', '.hentai.org.cn', '0', '\0', 'round-robin');
INSERT INTO `rule` VALUES ('10', '16777217', '3758096382', '0', '235900', 'contains', '.uuuu.com', '0', '\0', 'round-robin');
INSERT INTO `rule` VALUES ('16', '16777217', '3758096382', '0', '235959', 'prefix', '.kkkk.com', '0', '', 'random');
INSERT INTO `rule` VALUES ('18', '16777217', '3758096382', '0', '235959', 'suffix', '.baidu.com', '0', '', 'round-robin');
INSERT INTO `rule` VALUES ('24', '16777217', '3758096382', '0', '235959', 'prefix', 'www.uiop.', '0', '', 'round-robin');
INSERT INTO `rule` VALUES ('25', '16777217', '3758096382', '0', '235959', 'suffix', '.hhhh.com.cn', '0', '', 'random');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(20) NOT NULL COMMENT '用户类型，sa或admin',
  `name` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(32) NOT NULL COMMENT '混合salt加密后的密码',
  `salt` varchar(16) DEFAULT NULL COMMENT '为密码加密的盐',
  `accesstoken` varchar(32) DEFAULT NULL COMMENT '当前登陆令牌信息',
  `nonce` varchar(16) DEFAULT NULL,
  `enabled` bit(1) DEFAULT b'1' COMMENT '用户是否已经启用',
  `lastLoginTime` datetime DEFAULT NULL COMMENT '最近登陆时间',
  `lastLoginIP` varchar(50) DEFAULT NULL COMMENT '最近登陆IP',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_idx` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'sa', 'admin', 'ccf1ec65be67beb2852027f918003108', 'YCX9q803N4pur9if', '1c6c37aee6c22dbe53c0a851d3d9b06d', 'eBdfltWNNFz8Loxb', '', '2021-05-20 10:31:16', '0:0:0:0:0:0:0:1');
