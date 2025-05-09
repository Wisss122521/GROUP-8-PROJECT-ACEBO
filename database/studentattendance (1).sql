-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:4306
-- Generation Time: May 09, 2025 at 11:45 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `studentattendance`
--

-- --------------------------------------------------------

--
-- Table structure for table `attendance_records`
--

CREATE TABLE `attendance_records` (
  `attendance_id` int(255) NOT NULL,
  `students_name` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `class_id` varchar(255) NOT NULL,
  `date` date DEFAULT NULL,
  `time_in` time DEFAULT NULL,
  `time_out` time DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `attendance_records`
--

INSERT INTO `attendance_records` (`attendance_id`, `students_name`, `gender`, `class_id`, `date`, `time_in`, `time_out`, `status`) VALUES
(967, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-01', '22:51:04', '22:51:05', 'Present'),
(968, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-01', NULL, NULL, 'Absent'),
(969, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-02', '22:51:04', '22:51:05', 'Present'),
(970, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-02', NULL, NULL, 'Absent'),
(971, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-03', '22:51:04', '22:51:05', 'Present'),
(972, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-03', NULL, NULL, 'Absent'),
(973, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-04', '22:51:04', '22:51:05', 'Present'),
(974, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-04', NULL, NULL, 'Absent'),
(975, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-07', '22:51:04', '22:51:05', 'Present'),
(976, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-07', NULL, NULL, 'Absent'),
(977, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-08', '22:51:04', '22:51:05', 'Present'),
(978, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-08', NULL, NULL, 'Absent'),
(979, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-09', '22:51:04', '22:51:05', 'Present'),
(980, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-09', NULL, NULL, 'Absent'),
(981, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-10', '22:51:04', '22:51:05', 'Present'),
(982, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-10', NULL, NULL, 'Absent'),
(983, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-11', '22:51:04', '22:51:05', 'Present'),
(984, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-11', NULL, NULL, 'Absent'),
(985, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-14', '22:51:04', '22:51:05', 'Present'),
(986, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-14', NULL, NULL, 'Absent'),
(987, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-15', '22:51:04', '22:51:05', 'Present'),
(988, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-15', NULL, NULL, 'Absent'),
(989, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-16', '22:51:04', '22:51:05', 'Present'),
(990, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-16', NULL, NULL, 'Absent'),
(991, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-17', '22:51:04', '22:51:05', 'Present'),
(992, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-17', NULL, NULL, 'Absent'),
(993, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-18', '22:51:04', '22:51:05', 'Present'),
(994, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-18', NULL, NULL, 'Absent'),
(995, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-21', '22:51:04', '22:51:05', 'Present'),
(996, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-21', NULL, NULL, 'Absent'),
(997, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-22', '22:51:04', '22:51:05', 'Present'),
(998, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-22', NULL, NULL, 'Absent'),
(999, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-23', '22:51:04', '22:51:05', 'Present'),
(1000, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-23', NULL, NULL, 'Absent'),
(1001, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-24', '22:51:04', '22:51:05', 'Present'),
(1002, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-24', NULL, NULL, 'Absent'),
(1003, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-25', '22:51:04', '22:51:05', 'Present'),
(1004, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-25', NULL, NULL, 'Absent'),
(1005, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-28', '22:51:04', '22:51:05', 'Present'),
(1006, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-28', NULL, NULL, 'Absent'),
(1007, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-29', '22:51:04', '22:51:05', 'Present'),
(1008, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-29', NULL, NULL, 'Absent'),
(1009, 'ROB, RONNEL', 'MALE ', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-30', '22:51:04', '22:51:05', 'Present'),
(1010, 'CHAN, MARIE', 'FEMALE', 'GRADE 1 - MANGO - SY. 2024-2025', '2025-04-30', NULL, NULL, 'Absent');

-- --------------------------------------------------------

--
-- Table structure for table `classes`
--

CREATE TABLE `classes` (
  `class_id` int(255) NOT NULL,
  `class_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `classes`
--

INSERT INTO `classes` (`class_id`, `class_name`) VALUES
(28, 'GRADE 1 - MANGO - SY. 2024-2025');

-- --------------------------------------------------------

--
-- Table structure for table `class_subject`
--

CREATE TABLE `class_subject` (
  `id` int(255) NOT NULL,
  `class_id` int(255) NOT NULL,
  `subject_id` int(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `class_subject`
--

INSERT INTO `class_subject` (`id`, `class_id`, `subject_id`) VALUES
(56, 28, 20);

-- --------------------------------------------------------

--
-- Table structure for table `class_teacher`
--

CREATE TABLE `class_teacher` (
  `id` int(255) NOT NULL,
  `class_id` int(255) NOT NULL,
  `teacher_id` int(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `class_teacher`
--

INSERT INTO `class_teacher` (`id`, `class_id`, `teacher_id`) VALUES
(22, 28, 26);

-- --------------------------------------------------------

--
-- Table structure for table `inactive_students`
--

CREATE TABLE `inactive_students` (
  `id` int(255) NOT NULL,
  `students_id` int(255) DEFAULT NULL,
  `students_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `class_name` varchar(255) DEFAULT NULL,
  `parent_contact_number` varchar(255) DEFAULT NULL,
  `qr_code_data` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Inactive'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `login_table`
--

CREATE TABLE `login_table` (
  `user_id` int(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `login_table`
--

INSERT INTO `login_table` (`user_id`, `username`, `password`) VALUES
(1, 'Teacher', '!TcDefaultAccount');

-- --------------------------------------------------------

--
-- Table structure for table `qrcode`
--

CREATE TABLE `qrcode` (
  `qrcodeid` int(255) NOT NULL,
  `qrcodedata` varchar(255) NOT NULL,
  `qrcodefilepath` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `sms_logs`
--

CREATE TABLE `sms_logs` (
  `id` int(255) NOT NULL,
  `parent_number` varchar(255) NOT NULL,
  `student_name` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `status` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `students_id` int(255) NOT NULL,
  `students_name` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `class_id` int(255) NOT NULL,
  `parent_contact_number` varchar(255) DEFAULT NULL,
  `qr_code_data` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL DEFAULT 'Active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`students_id`, `students_name`, `gender`, `class_id`, `parent_contact_number`, `qr_code_data`, `status`) VALUES
(10737, 'ROB, RONNEL', 'MALE ', 28, '639000000000', NULL, 'Active'),
(10741, 'CHAN, MARIE', 'FEMALE', 28, '639217467275', NULL, 'Active');

-- --------------------------------------------------------

--
-- Table structure for table `subject`
--

CREATE TABLE `subject` (
  `subject_id` int(255) NOT NULL,
  `subject_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subject`
--

INSERT INTO `subject` (`subject_id`, `subject_name`) VALUES
(20, 'MATH');

-- --------------------------------------------------------

--
-- Table structure for table `teachers`
--

CREATE TABLE `teachers` (
  `teacher_id` int(255) NOT NULL,
  `teacher_name` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teachers`
--

INSERT INTO `teachers` (`teacher_id`, `teacher_name`, `gender`) VALUES
(26, 'Santos, Maria A.', 'FEMALE');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attendance_records`
--
ALTER TABLE `attendance_records`
  ADD PRIMARY KEY (`attendance_id`),
  ADD KEY `students_attendance` (`students_name`);

--
-- Indexes for table `classes`
--
ALTER TABLE `classes`
  ADD PRIMARY KEY (`class_id`);

--
-- Indexes for table `class_subject`
--
ALTER TABLE `class_subject`
  ADD PRIMARY KEY (`id`),
  ADD KEY `class_id_combine` (`class_id`),
  ADD KEY `subject_id_combine` (`subject_id`);

--
-- Indexes for table `class_teacher`
--
ALTER TABLE `class_teacher`
  ADD PRIMARY KEY (`id`),
  ADD KEY `class_classes_id` (`class_id`),
  ADD KEY `class_teacher_id` (`teacher_id`);

--
-- Indexes for table `inactive_students`
--
ALTER TABLE `inactive_students`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `login_table`
--
ALTER TABLE `login_table`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `username_2` (`username`),
  ADD KEY `username_3` (`username`);

--
-- Indexes for table `qrcode`
--
ALTER TABLE `qrcode`
  ADD PRIMARY KEY (`qrcodeid`);

--
-- Indexes for table `sms_logs`
--
ALTER TABLE `sms_logs`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`students_id`),
  ADD KEY `class_student` (`class_id`);

--
-- Indexes for table `subject`
--
ALTER TABLE `subject`
  ADD PRIMARY KEY (`subject_id`);

--
-- Indexes for table `teachers`
--
ALTER TABLE `teachers`
  ADD PRIMARY KEY (`teacher_id`),
  ADD UNIQUE KEY `teacher_name` (`teacher_name`),
  ADD UNIQUE KEY `teacher_name_2` (`teacher_name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance_records`
--
ALTER TABLE `attendance_records`
  MODIFY `attendance_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1011;

--
-- AUTO_INCREMENT for table `classes`
--
ALTER TABLE `classes`
  MODIFY `class_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `class_subject`
--
ALTER TABLE `class_subject`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=57;

--
-- AUTO_INCREMENT for table `class_teacher`
--
ALTER TABLE `class_teacher`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `inactive_students`
--
ALTER TABLE `inactive_students`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `login_table`
--
ALTER TABLE `login_table`
  MODIFY `user_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT for table `qrcode`
--
ALTER TABLE `qrcode`
  MODIFY `qrcodeid` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=208;

--
-- AUTO_INCREMENT for table `sms_logs`
--
ALTER TABLE `sms_logs`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=123;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `students_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10742;

--
-- AUTO_INCREMENT for table `subject`
--
ALTER TABLE `subject`
  MODIFY `subject_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `teachers`
--
ALTER TABLE `teachers`
  MODIFY `teacher_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `class_subject`
--
ALTER TABLE `class_subject`
  ADD CONSTRAINT `class_id_combine` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  ADD CONSTRAINT `subject_id_combine` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`subject_id`);

--
-- Constraints for table `class_teacher`
--
ALTER TABLE `class_teacher`
  ADD CONSTRAINT `class_classes_id` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  ADD CONSTRAINT `class_teacher_id` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`);

--
-- Constraints for table `students`
--
ALTER TABLE `students`
  ADD CONSTRAINT `class_student` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
