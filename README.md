CREATE database animalwelfaresyst

USE animalwelfaresyst

### 1. **User and Role Management**

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users_roles (
    user_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);


### 2. **Shelter Management**

CREATE TABLE shelters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    current_occupancy INT,
    contact_details VARCHAR(255)
);


### 3. **Animal Management**

CREATE TABLE animals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    health_status VARCHAR(255) NOT NULL,
    picture_data LONGBLOB,
    shelter_id BIGINT,
    adoption_status ENUM('AVAILABLE', 'ADOPTED', 'NOT_AVAILABLE') DEFAULT 'AVAILABLE',
    FOREIGN KEY (shelter_id) REFERENCES shelters(id)
);


### 4. **Adoption Management**

CREATE TABLE adoption (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL,
    request_date DATE NOT NULL,
    adoption_date DATE,
    score INT NOT NULL DEFAULT 0,
    FOREIGN KEY (animal_id) REFERENCES animals(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE adoption_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    adoption_id BIGINT NOT NULL,
    answer VARCHAR(255) NOT NULL,
    FOREIGN KEY (adoption_id) REFERENCES adoption(id) ON DELETE CASCADE
);


### 5. **Incident Reporting**

CREATE TABLE report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255) NOT NULL,  -- Enum values 'ABUSE' or 'ACCIDENT'
    description TEXT NOT NULL,
    animal_id BIGINT,            -- Foreign key to animal table, nullable
    location VARCHAR(255) NOT NULL,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,     -- Foreign key to user table
    address VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'pending',
    rejection_reason VARCHAR(255),
    FOREIGN KEY (animal_id) REFERENCES animals(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE attachment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(255) NOT NULL,  -- e.g., image/jpeg, video/mp4
    data LONGBLOB NOT NULL,  -- Store the file data
    report_id BIGINT NOT NULL,  -- Foreign key to report table
    FOREIGN KEY (report_id) REFERENCES report(id)
);


### 6. **Donation Management**

CREATE TABLE donation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    donation_type ENUM('FOOD', 'SUPPLIES', 'HEALTH_HYGIENE', 'TOYS', 'EQUIPMENT', 'MONEY', 'VOLUNTEER', 'EDUCATIONAL', 'OTHER') NOT NULL,
    amount DECIMAL(10, 2),
    quantity INT,
    donor_name VARCHAR(255),
    mobile_number VARCHAR(15),
    email VARCHAR(255),
    address VARCHAR(255),
    date DATE,
    recurring BOOLEAN NOT NULL DEFAULT FALSE,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);


