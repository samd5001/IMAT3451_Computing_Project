<?php
class SQLWrapper {
 
    private $db;
 
    function __construct() {
        require_once "db_config.php";
        $this->db = new mysqli(host, user, password);
    }

    function __destruct() {}

    public function storeUser($email, $password, $name, $dob, $gender, $height, $weight, $goal) {
        $this->db->select_db("users");
        $hash = $this->hashPassword($password);
        $stmt = $this->db->prepare("INSERT INTO user (email, passwordHash, passwordSalt, name, dob, gender, height, weight, goal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("sssssiddi", $email, $hash["passwordHash"], $hash["salt"], $name, $dob, $gender, $height, $weight, $goal);
        $result = $stmt->execute();

        if ($result) {
            $stmt = $this->db->prepare("SELECT * FROM user WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
            $this->db->select_db("data");
            $this->db->query("CREATE TABLE `". $email . "exercises` (
                                name varchar(30) NOT NULL UNIQUE PRIMARY KEY,
                                description varchar(400) NOT NULL,
                                type tinyint NOT NULL,
                                imageURL varchar(50), 
                                minThreshold int,
                                maxThreshold int,
                                areasWorked varchar(50),
                                userMade tinyint NOT NULL
                                )");
            $this->db->query("CREATE TABLE `" . $email . "plans`(
                            name varchar(40) NOT NULL UNIQUE PRIMARY KEY,
                            description varchar(300) NOT NULL,
                            days varchar (30) NOT NULL,
                            userMade tinyint NOT NULL
                            )");
            $this->db->query("CREATE TABLE `" . $email . "days`(
                                    planName varchar(40) NOT NULL,
                                    dayNumber int NOT NULL,
                                    exercises varchar(200) NOT NULL,
                                    sets varchar(100) NOT NULL,
                                    reps varchar(200) NOT NULL,
                                    PRIMARY KEY (planName, dayNumber),
                                    FOREIGN KEY (planName) REFERENCES `" . $email . "plans`(name) ON DELETE CASCADE
                                    )");
            $this->db->query("CREATE TABLE `" . $email . "records`(
                                    id int NOT NULL,
                                    exerciseName varchar(30) NOT NULL,
                                    planName varchar(40),
                                    dayNumber TINYINT,
                                    timeDone DATETIME NOT NULL,
                                    sets text,
                                    PRIMARY KEY (id, timeDone),
                                    FOREIGN KEY (exerciseName) REFERENCES  `" . $email . "exercises`(name) ON DELETE CASCADE
                                    )");
            $this->db->query("INSERT INTO `" . $email ."exercises`
                                    SELECT *
                                    FROM exercises");
            $this->db->query("INSERT INTO `" . $email ."plans`
                                    SELECT *
                                    FROM plans");
            $this->db->query("INSERT INTO `" . $email ."days`
                                    SELECT *
                                    FROM days");
            $this->db->select_db($email."data");
            unset($user['passwordHash'], $user['passwordSalt']);
            return $user;
        } else {
            return false;
        }

    }

    public function updateUser($email, $newemail, $name, $goal) {
        $this->db->select_db("users");
        $this->db->query("UPDATE user SET email = '" . $newemail . "', name = '" . $name ."' , goal = " . $goal ." WHERE email = '" . $email . "'");
        $this->db->select_db("data");
        var_dump("ALTER TABLE `" . $email . "exercises` RENAME TO `" . $newemail . "exercises`");
        $this->db->query("ALTER TABLE `" . $email . "exercises` RENAME TO `" . $newemail . "exercises`");
        $this->db->query("ALTER TABLE `" . $email . "plans` RENAME TO `" . $newemail . "plans`");
        $this->db->query("ALTER TABLE `" . $email . "days` RENAME TO `" . $newemail . "days`");
        $this->db->query("ALTER TABLE `" . $email . "records` RENAME TO `" . $newemail . "records`");
    }

    public function deleteUser($email) {
        $this->db->select_db("data");
        $this->db->query("DROP TABLE `". $email . "records`");
        $this->db->query("DROP TABLE `". $email . "days`");
        $this->db->query("DROP TABLE `". $email . "plans`");
        $this->db->query("DROP TABLE `". $email . "exercises`");
        $this->db->select_db("users");
        $this->db->query("DELETE FROM user WHERE email = '" . $email . "'");
    }

    public function resetUser($email) {
        $this->db->select_db("data");
        $this->db->query("DROP TABLE `". $email . "records`");
        $this->db->query("DROP TABLE `". $email . "days`");
        $this->db->query("DROP TABLE `". $email . "plans`");
        $this->db->query("DROP TABLE `". $email . "exercises`");
        $this->db->query("CREATE TABLE `". $email . "exercises` (
                                name varchar(30) NOT NULL UNIQUE PRIMARY KEY,
                                description varchar(400) NOT NULL,
                                type tinyint NOT NULL,
                                imageURL varchar(50), 
                                minThreshold int,
                                maxThreshold int,
                                areasWorked varchar(50),
                                userMade tinyint NOT NULL
                                )");
        $this->db->query("CREATE TABLE `" . $email . "plans`(
                                name varchar(40) NOT NULL UNIQUE PRIMARY KEY,
                            description varchar(300) NOT NULL,
                            days varchar (30) NOT NULL,
                            userMade tinyint NOT NULL
                            )");
        $this->db->query("CREATE TABLE `" . $email . "days`(
                                    planName varchar(40) NOT NULL,
                                    dayNumber int NOT NULL,
                                    exercises varchar(300) NOT NULL,
                                    sets varchar(100) NOT NULL,
                                    reps varchar(100) NOT NULL,
                                    PRIMARY KEY (planName, dayNumber),
                                    FOREIGN KEY (planName) REFERENCES `" . $email . "plans`(name) ON DELETE CASCADE
                                    )");
        $this->db->query("CREATE TABLE `" . $email . "records`(
                                    id int NOT NULL,
                                    exerciseName varchar(30) NOT NULL,
                                    planName varchar(40),
                                    dayNumber TINYINT,
                                    timeDone DATETIME NOT NULL,
                                    sets text,
                                    PRIMARY KEY (id, timeDone),
                                    FOREIGN KEY (exerciseName) REFERENCES  `" . $email . "exercises`(name) ON DELETE CASCADE
                                    )");
        $this->db->query("INSERT INTO `" . $email ."exercises`
                                    SELECT *
                                    FROM exercises");
        $this->db->query("INSERT INTO `" . $email ."plans`
                                    SELECT *
                                    FROM plans");
        $this->db->query("INSERT INTO `" . $email ."days`
                                    SELECT *
                                    FROM days");
    }

    public function updatePassword($email, $password) {
        $this->db->select_db("users");
        $hash = $this->hashPassword($password);
        $stmt = $this->db->prepare("UPDATE user SET passwordHash = ?, passwordSalt = ? WHERE email = ?");
        $stmt->bind_param("sss", $hash["passwordHash"], $hash["salt"], $email);
        $stmt->execute();
        $stmt->close();
    }

    public function getUser($email, $password) {
        $this->db->select_db("users");
        $stmt = $this->db->prepare("SELECT * FROM user WHERE email = ?");
        $stmt->bind_param("s", $email);
        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();

            $salt = $user['passwordSalt'];
            $passwordHash = $user['passwordHash'];
            $hash = $this->checkHash($salt, $password);
            if ($passwordHash == $hash) {
                return $user;
            }
        } else {
            return NULL;
        }
    }

    public function getExercises() {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT * FROM exercises");
        $values = NULL;
        if ($stmt->execute()) {
            $exercises = $stmt->get_result();
            if ($exercises->num_rows > 0)
            {
                while($row = $exercises->fetch_assoc())
                {
                    $values[] = $row;
                }
            }
        }
        return $values;
    }

    public function getUserExercises($email) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT * FROM `" . $email . "exercises` WHERE userMade = 1");
        $values = NULL;
        if ($stmt->execute()) {
            $exercises = $stmt->get_result();
            if ($exercises->num_rows > 0)
            {
                while($row = $exercises->fetch_assoc())
                {
                    $values[] = $row;
                }
            }
        }
        return $values;
    }

    public function getUserExercisesName($email) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT name FROM `" . $email . "exercises` WHERE userMade = 1");
        $values = NULL;
        if ($stmt->execute()) {
            $exercises = $stmt->get_result();
            if ($exercises->num_rows > 0)
            {
                while($row = $exercises->fetch_assoc())
                {
                    $values[] = $row['name'];
                }
            }
            else
            {
                $values[]['name'] = "";
            }
        }
        return $values;
    }

    public function storeExercise($email, $name, $description, $type, $url, $min, $max, $areas, $user) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("INSERT INTO `" . $email . "exercises` (name, description, type, imageURL, minThreshold, maxThreshold, areasWorked, userMade) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("ssisiisi", $name, $description, $type, $url, $min, $max, $areas, $user);
        return $stmt->execute();
    }

    public function deleteExercise($email, $name) {
        $this->db->select_db("data");
        $this->db->prepare("DELETE FROM `" . $email . "exercises` WHERE name = '" . $name . "'")->execute();
    }

    public function getPlans() {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT * FROM plans");
        $values = NULL;
        if ($stmt->execute()) {
            $plans = $stmt->get_result();
            if ($plans->num_rows > 0)
            {
                while($row = $plans->fetch_assoc())
                {
                    $values[] =  $row;
                }
            }
        }
        return $values;
    }

    public function getUserPlans($email) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT * FROM `" . $email . "plans` WHERE userMade = 1");
        $values = NULL;
        if ($stmt->execute()) {
            $plans = $stmt->get_result();
            if ($plans->num_rows > 0)
            {
                while($row = $plans->fetch_assoc())
                {
                    $values[] =  $row;
                }
            }
        }
        return $values;
    }

    public function getUserPlansName($email) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT name FROM `" . $email . "plans` WHERE userMade = 1");
        $values = NULL;
        if ($stmt->execute()) {
            $exercises = $stmt->get_result();
            if ($exercises->num_rows > 0)
            {
                while($row = $exercises->fetch_assoc())
                {
                    $values[] = $row['name'];
                }
            }
        }
        return $values;
    }

    public function deletePlan($email, $name) {
        $this->db->select_db("data");
        $this->db->prepare("DELETE FROM `" . $email . "exercises` WHERE name = `" . $name . "`")->execute();
    }

    public function getDays() {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT * FROM days");
        $values = NULL;
        if ($stmt->execute()) {
            $days = $stmt->get_result();
            if ($days->num_rows > 0)
            {
                while($row = $days->fetch_assoc())
                {
                    $values[] = $row;
                }
            }
        }
        return $values;
    }

    public function getUserDays($email) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT * FROM `" . $email . "days` 
                                        WHERE planName IN 
                                        (SELECT name FROM `" . $email . "plans` 
                                        WHERE userMade = 1)");
        $values = NULL;
        if ($stmt->execute()) {
            $days = $stmt->get_result();
            if ($days->num_rows > 0)
            {
                while($row = $days->fetch_assoc())
                {
                    $values[] = $row;
                }
            }
        }
        return $values;
    }

    public function deleteDays($email) {

    }

    public function getRecords($email) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT * FROM `" . $email . "records`");
        $values = NULL;
        if ($stmt->execute()) {
            $records = $stmt->get_result();
            if ($records->num_rows > 0)
            {
                while($row = $records->fetch_assoc())
                {
                    $row['sets'] = json_decode($row['sets']);
                    $values[] = $row;
                }
            }
        }
        return $values;
    }

    public function getNewRecords($email, $lastid) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT * FROM `" . $email . "records` WHERE id > " . $lastid);
        $values = NULL;
        if ($stmt->execute()) {
            $records = $stmt->get_result();
            if ($records->num_rows > 0)
            {
                while($row = $records->fetch_assoc())
                {
                    $row['sets'] = json_decode($row['sets']);
                    $values[] = $row;
                }
            }
        }
        return $values;
    }

    public function getRecordIDs($email) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT id, timeDone FROM `" . $email . "records`");
        $values = NULL;
        if ($stmt->execute()) {
            $exercises = $stmt->get_result();
            if ($exercises->num_rows > 0)
            {
                while($row = $exercises->fetch_assoc())
                {
                    $values[] = $row;
                }
            }
            else
            {
                $values[0]['id'] = 0;
                $values[0]['timeDone'] = "1990/01/01 00:00:00";
            }
        }
        return $values;
    }

    public function getLastRecordID($email) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT MAX(id) FROM `" . $email . "records`");
        $lastid = NULL;
        if ($stmt->execute()) {
            $row = $stmt->get_result()->fetch_assoc();
            $lastid = $row['id'];
        }
        return $lastid;
    }

    public function storeRecord($email, $id, $ename, $pname, $day, $time, $set) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("INSERT INTO `" . $email . "records` (id, exerciseName, planName, dayNumber, timeDone, sets) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("ississ", $id, $ename, $pname, $day, $time, $set);
        return $stmt->execute();
    }

    public function deleteRecord($email, $id) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("DELETE FROM `" . $email . "records` WHERE id = ?");
        $stmt->bind_param("i", $id);
        return $stmt->execute();
    }

    public function checkUser($email) {
        $this->db->select_db("users");
        $stmt = $this->db->prepare("SELECT * FROM user WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        if ($stmt->get_result()->num_rows > 0) {
            // user existed
            $stmt->close();
            return true;
        } else {
            $stmt->close();
            return false;
        }
    }

    public function checkRowCounts($email) {
        $this->db->select_db("data");
        $stmt = $this->db->prepare("SELECT id FROM `" . $email . "records`");
        $values = NULL;
        if ($stmt->execute()) {
            $records = $stmt->get_result();
            $values['records'] = $records->num_rows;
        }
        $stmt = $this->db->prepare("SELECT name FROM `" . $email . "exercises`");
        if ($stmt->execute()) {
            $exercises = $stmt->get_result();
            $values['exercises'] = $exercises->num_rows;
        }
        $stmt = $this->db->prepare("SELECT name FROM `" . $email . "plans`");
        if ($stmt->execute()) {
            $plans = $stmt->get_result();
            $values['plans'] = $plans->num_rows;
        }
        $stmt = $this->db->prepare("SELECT planName FROM `" . $email . "days`");
        if ($stmt->execute()) {
            $days = $stmt->get_result();
            $values['days'] = $days->num_rows;
        }
        return $values;
    }
 
    public function hashPassword($password) {
        $salt = sha1(rand(10000, 1000000000));
        $salt = substr($salt, 0, 10);
        $passwordHash = base64_encode(sha1($password . $salt, true));
        $hash = array("salt" => $salt, "passwordHash" => $passwordHash);
        return $hash;
    }

    public function checkHash($salt, $password) {
        $hash = base64_encode(sha1($password . $salt, true));
        return $hash;
    }


 
}