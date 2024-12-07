-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 07, 2024 at 04:31 PM
-- Server version: 10.4.27-MariaDB
-- PHP Version: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vendingmachine1`
--

-- --------------------------------------------------------

--
-- Table structure for table `drink`
--

CREATE TABLE `drink` (
  `name` varchar(100) NOT NULL,
  `image_path` varchar(255) NOT NULL,
  `stock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `drink`
--

INSERT INTO `drink` (`name`, `image_path`, `stock`) VALUES
('CocaCola', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\minuman\\cocacola.png', 10),
('Water', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\minuman\\water.jpeg', 5),
('Orange Juice', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\minuman\\orange_juice.jpg', 12),
('Milk', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\minuman\\milk.jpg', 15),
('Tea', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\minuman\\tea.jpg', 9);

-- --------------------------------------------------------

--
-- Table structure for table `images`
--

CREATE TABLE `images` (
  `id` int(11) NOT NULL,
  `imageName` varchar(255) DEFAULT NULL,
  `imagePath` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `images`
--

INSERT INTO `images` (`id`, `imageName`, `imagePath`) VALUES
(1, 'WelcomeImage', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1/VM 1.png');

-- --------------------------------------------------------

--
-- Table structure for table `snack`
--

CREATE TABLE `snack` (
  `name` varchar(100) NOT NULL,
  `image_path` varchar(255) NOT NULL,
  `stock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `snack`
--

INSERT INTO `snack` (`name`, `image_path`, `stock`) VALUES
('Chips', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\makanan\\chips.jpg', 8),
('Candy', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\makanan\\candy.jpg', 15),
('Chocolate Bar', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\makanan\\chocolate_bar.jpg', 10),
('Biscuits', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\makanan\\biscuits.jpg', 5),
('Cookies', 'D:\\SEMESTER 5\\PBO\\PROJECT\\Vending Machine 1\\assets\\makanan\\cookies.jpg', 12);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `images`
--
ALTER TABLE `images`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `images`
--
ALTER TABLE `images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
