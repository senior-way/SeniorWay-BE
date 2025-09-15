-- Users 테이블
CREATE TABLE `Users` (
                         `user_id` BIGINT NOT NULL AUTO_INCREMENT,
                         `email` VARCHAR(255) NOT NULL UNIQUE,
                         `password` VARCHAR(255) NOT NULL,
                         `name` VARCHAR(100) NOT NULL,
                         `login_type` ENUM('KAKAO','LOCAL') NOT NULL,
                         `create_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                         `update_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `role` ENUM('USER', 'GUARDIANS') NOT NULL,
                         PRIMARY KEY (`user_id`)
);

-- user_preferences
CREATE TABLE `user_preferences` (
                                    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK: 사용자 선호 프로파일 ID',
                                    `user_id` BIGINT NOT NULL,
                                    `preffered_category` VARCHAR(255) NOT NULL COMMENT '자연, 역사문화예술, 먹거리, 쇼핑, 체험액티비티, 상관없음 중 선택값을 콤마로 저장',
                                    `preferred_transportation` VARCHAR(255) NOT NULL COMMENT '기차, 버스, 비행기, 자차, 자전거, 도보 중 선택값을 콤마로 저장',
                                    `wheelchair_usage` TINYINT NOT NULL COMMENT '휠체어 사용 여부 (1: 사용, 0: 사용하지 않음)',
                                    `pet_companion` TINYINT NOT NULL COMMENT '반려동물 동반 여부 (1: 동반, 0: 동반하지 않음)',
                                    `digital_literacy` ENUM('상', '중', '하') NOT NULL COMMENT '전자기기 활용 능력 수준',
                                    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '레코드 생성일시',
                                    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '레코드 수정일시',
                                    PRIMARY KEY (`id`),
                                    FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`)
);

-- tourist_spot
CREATE TABLE `tourist_spot` (
                                `tourist_spot_id` BIGINT NOT NULL AUTO_INCREMENT,
                                `content_id` VARCHAR(50) NOT NULL,
                                `content_type_id` VARCHAR(20),
                                `title` VARCHAR(255),
                                `tel` VARCHAR(100),
                                `zipcode` VARCHAR(20),
                                `addr1` VARCHAR(255),
                                `addr2` VARCHAR(255),
                                `areacode` VARCHAR(20),
                                `sigungucode` VARCHAR(20),
                                `mapx` VARCHAR(50),
                                `mapy` VARCHAR(50),
                                `mlevel` VARCHAR(10),
                                `firstimage` TEXT,
                                `firstimage2` TEXT,
                                `cat1` VARCHAR(20),
                                `cat2` VARCHAR(20),
                                `cat3` VARCHAR(20),
                                `lcls_systm1` VARCHAR(100),
                                `lcls_systm2` VARCHAR(100),
                                `lcls_systm3` VARCHAR(100),
                                `lDongRegnCd` VARCHAR(20),
                                `lDongSignguCd` VARCHAR(20),
                                `cpyrhtDivCd` VARCHAR(20),
                                `created_time` DATETIME,
                                `modified_time` DATETIME,
                                PRIMARY KEY (`tourist_spot_id`)
);

-- tourist_audio_guides
CREATE TABLE `tourist_audio_guides` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK: 오디오 가이드 ID',
                                        `tourist_spot_id` BIGINT NOT NULL,
                                        `title` VARCHAR(255) NOT NULL COMMENT '이야기 키워드 제목',
                                        `map_x` VARCHAR(50),
                                        `map_y` VARCHAR(50),
                                        `audio_title` VARCHAR(255),
                                        `script` TEXT,
                                        `play_time` VARCHAR(50),
                                        `audio_url` VARCHAR(500),
                                        `lang_code` VARCHAR(10),
                                        `image_url` VARCHAR(500),
                                        `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '레코드 생성일시',
                                        `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '레코드 수정일시',
                                        PRIMARY KEY (`id`),
                                        FOREIGN KEY (`tourist_spot_id`) REFERENCES `tourist_spot` (`tourist_spot_id`)
);

-- Schedule
CREATE TABLE `Schedule` (
                            `schedule_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '일정 ID',
                            `user_id` BIGINT NOT NULL,
                            `title` VARCHAR(100) NOT NULL COMMENT '일정 제목',
                            `start_date` DATETIME,
                            `end_date` DATETIME,
                            `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '일정 생성 일시',
                            `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '일정 수정 일시',
                            PRIMARY KEY (`schedule_id`),
                            FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`)
);

-- Schedule_TouristSpot
CREATE TABLE `Schedule_TouristSpot` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                        `schedule_id` BIGINT NOT NULL COMMENT '일정 ID',
                                        `tourist_spot_id` BIGINT NOT NULL,
                                        `sequence_order` INT NOT NULL COMMENT '방문 순서',
                                        `stay_time` INT COMMENT '관광지 체류 시간(분)',
                                        `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '관광지 선택 일시',
                                        PRIMARY KEY (`id`),
                                        FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`),
                                        FOREIGN KEY (`tourist_spot_id`) REFERENCES `tourist_spot` (`tourist_spot_id`)
);

-- user_guardian_links
CREATE TABLE `user_guardian_links` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                                       `user_id` BIGINT NOT NULL,
                                       `guardian_id` BIGINT NOT NULL COMMENT '보호자 ID',
                                       `relation` VARCHAR(50) DEFAULT NULL COMMENT '관계 (예: 자녀, 배우자 등)',
                                       `is_primary` BOOLEAN DEFAULT FALSE COMMENT '대표 보호자인지 여부',
                                       `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       PRIMARY KEY (`id`),
                                       FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`),
                                       FOREIGN KEY (`guardian_id`) REFERENCES `Users` (`user_id`),
                                       UNIQUE (`user_id`, `guardian_id`)
);
