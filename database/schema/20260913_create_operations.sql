-- 適用前にバックアップ・アプリ停止。旧配車データは削除しない。SQL Server 2017以降。
SET XACT_ABORT ON;
BEGIN TRANSACTION;
IF OBJECT_ID('dbo.operation_vehicle','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_vehicle (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  state INT NOT NULL DEFAULT 0,
  regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  code NVARCHAR(120) NULL,
  name NVARCHAR(120) NULL,
  plate_number NVARCHAR(120) NULL,
  vehicle_type NVARCHAR(120) NULL,
  capacity INT NULL,
  recorder_id NVARCHAR(120) NULL,
  remarks NVARCHAR(1000) NULL
 );
 CREATE UNIQUE INDEX UX_operation_vehicle_code ON dbo.operation_vehicle(code) WHERE state=0;
END;
IF OBJECT_ID('dbo.operation_vehicle_log','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_vehicle_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
  id BIGINT NULL,
  version INT NULL,
  state INT NULL,
  regist_date DATETIME2 NULL,
  update_date DATETIME2 NULL,
  code NVARCHAR(120) NULL,
  name NVARCHAR(120) NULL,
  plate_number NVARCHAR(120) NULL,
  vehicle_type NVARCHAR(120) NULL,
  capacity INT NULL,
  recorder_id NVARCHAR(120) NULL,
  remarks NVARCHAR(1000) NULL,
  editor NVARCHAR(255) NOT NULL,
  process VARCHAR(20) NOT NULL,
  log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
 CREATE INDEX IX_operation_vehicle_log ON dbo.operation_vehicle_log(id,version);
END;
IF OBJECT_ID('dbo.operation_inspection','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_inspection (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  state INT NOT NULL DEFAULT 0,
  regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  vehicle_code NVARCHAR(120) NULL,
  kind NVARCHAR(120) NULL,
  due_date DATE NULL,
  scheduled_date DATE NULL,
  performed_date DATE NULL,
  unavailable_from DATE NULL,
  unavailable_to DATE NULL,
  odometer DECIMAL(12,2) NULL,
  cost DECIMAL(12,2) NULL,
  remarks NVARCHAR(1000) NULL,
  vehicle_id BIGINT NULL,
  FOREIGN KEY (vehicle_id) REFERENCES dbo.operation_vehicle(id)
 );
END;
IF OBJECT_ID('dbo.operation_inspection_log','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_inspection_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
  id BIGINT NULL,
  version INT NULL,
  state INT NULL,
  regist_date DATETIME2 NULL,
  update_date DATETIME2 NULL,
  vehicle_code NVARCHAR(120) NULL,
  kind NVARCHAR(120) NULL,
  due_date DATE NULL,
  scheduled_date DATE NULL,
  performed_date DATE NULL,
  unavailable_from DATE NULL,
  unavailable_to DATE NULL,
  odometer DECIMAL(12,2) NULL,
  cost DECIMAL(12,2) NULL,
  remarks NVARCHAR(1000) NULL,
  vehicle_id BIGINT NULL,
  editor NVARCHAR(255) NOT NULL,
  process VARCHAR(20) NOT NULL,
  log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
 CREATE INDEX IX_operation_inspection_log ON dbo.operation_inspection_log(id,version);
END;
IF OBJECT_ID('dbo.operation_crew','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_crew (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  state INT NOT NULL DEFAULT 0,
  regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  work_date DATE NULL,
  vehicle_code NVARCHAR(120) NULL,
  remarks NVARCHAR(1000) NULL,
  vehicle_id BIGINT NULL,
  FOREIGN KEY (vehicle_id) REFERENCES dbo.operation_vehicle(id)
 );
 CREATE UNIQUE INDEX UX_operation_crew_day_vehicle ON dbo.operation_crew(work_date,vehicle_id) WHERE state=0;
END;
IF OBJECT_ID('dbo.operation_crew_log','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_crew_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
  id BIGINT NULL,
  version INT NULL,
  state INT NULL,
  regist_date DATETIME2 NULL,
  update_date DATETIME2 NULL,
  work_date DATE NULL,
  vehicle_code NVARCHAR(120) NULL,
  remarks NVARCHAR(1000) NULL,
  vehicle_id BIGINT NULL,
  editor NVARCHAR(255) NOT NULL,
  process VARCHAR(20) NOT NULL,
  log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
 CREATE INDEX IX_operation_crew_log ON dbo.operation_crew_log(id,version);
END;
IF OBJECT_ID('dbo.operation_score','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_score (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  state INT NOT NULL DEFAULT 0,
  regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  work_date DATE NULL,
  vehicle_code NVARCHAR(120) NULL,
  employee_code NVARCHAR(120) NULL,
  score DECIMAL(12,2) NULL,
  remarks NVARCHAR(1000) NULL,
  vehicle_id BIGINT NULL,
  employee_id INT NULL,
  employee_name NVARCHAR(255) NULL,
  FOREIGN KEY (vehicle_id) REFERENCES dbo.operation_vehicle(id),
  FOREIGN KEY (employee_id) REFERENCES dbo.employees(employee_id)
 );
 CREATE UNIQUE INDEX UX_operation_score_day ON dbo.operation_score(work_date,vehicle_id,employee_id) WHERE state=0;
END;
IF OBJECT_ID('dbo.operation_score_log','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_score_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
  id BIGINT NULL,
  version INT NULL,
  state INT NULL,
  regist_date DATETIME2 NULL,
  update_date DATETIME2 NULL,
  work_date DATE NULL,
  vehicle_code NVARCHAR(120) NULL,
  employee_code NVARCHAR(120) NULL,
  score DECIMAL(12,2) NULL,
  remarks NVARCHAR(1000) NULL,
  vehicle_id BIGINT NULL,
  employee_id INT NULL,
  employee_name NVARCHAR(255) NULL,
  editor NVARCHAR(255) NOT NULL,
  process VARCHAR(20) NOT NULL,
  log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
 CREATE INDEX IX_operation_score_log ON dbo.operation_score_log(id,version);
END;
IF OBJECT_ID('dbo.operation_qualification_type','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_qualification_type (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  state INT NOT NULL DEFAULT 0,
  regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  code NVARCHAR(120) NULL,
  name NVARCHAR(120) NULL,
  category NVARCHAR(120) NULL,
  grade NVARCHAR(120) NULL,
  expiry_required BIT NULL,
  driver_license BIT NULL,
  remarks NVARCHAR(1000) NULL
 );
 CREATE UNIQUE INDEX UX_operation_qualification_type_code ON dbo.operation_qualification_type(code) WHERE state=0;
END;
IF OBJECT_ID('dbo.operation_qualification_type_log','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_qualification_type_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
  id BIGINT NULL,
  version INT NULL,
  state INT NULL,
  regist_date DATETIME2 NULL,
  update_date DATETIME2 NULL,
  code NVARCHAR(120) NULL,
  name NVARCHAR(120) NULL,
  category NVARCHAR(120) NULL,
  grade NVARCHAR(120) NULL,
  expiry_required BIT NULL,
  driver_license BIT NULL,
  remarks NVARCHAR(1000) NULL,
  editor NVARCHAR(255) NOT NULL,
  process VARCHAR(20) NOT NULL,
  log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
 CREATE INDEX IX_operation_qualification_type_log ON dbo.operation_qualification_type_log(id,version);
END;
IF OBJECT_ID('dbo.operation_qualification','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_qualification (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  state INT NOT NULL DEFAULT 0,
  regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  employee_code NVARCHAR(120) NULL,
  qualification_code NVARCHAR(120) NULL,
  acquired_date DATE NULL,
  expiry_date DATE NULL,
  renewal_date DATE NULL,
  certificate_number NVARCHAR(120) NULL,
  remarks NVARCHAR(1000) NULL,
  employee_id INT NULL,
  employee_name NVARCHAR(255) NULL,
  qualification_type_id BIGINT NULL,
  qualification_name NVARCHAR(120) NULL,
  FOREIGN KEY (employee_id) REFERENCES dbo.employees(employee_id),
  FOREIGN KEY (qualification_type_id) REFERENCES dbo.operation_qualification_type(id)
 );
END;
IF OBJECT_ID('dbo.operation_qualification_log','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_qualification_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
  id BIGINT NULL,
  version INT NULL,
  state INT NULL,
  regist_date DATETIME2 NULL,
  update_date DATETIME2 NULL,
  employee_code NVARCHAR(120) NULL,
  qualification_code NVARCHAR(120) NULL,
  acquired_date DATE NULL,
  expiry_date DATE NULL,
  renewal_date DATE NULL,
  certificate_number NVARCHAR(120) NULL,
  remarks NVARCHAR(1000) NULL,
  employee_id INT NULL,
  employee_name NVARCHAR(255) NULL,
  qualification_type_id BIGINT NULL,
  qualification_name NVARCHAR(120) NULL,
  editor NVARCHAR(255) NOT NULL,
  process VARCHAR(20) NOT NULL,
  log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
 CREATE INDEX IX_operation_qualification_log ON dbo.operation_qualification_log(id,version);
END;
IF OBJECT_ID('dbo.operation_labor','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_labor (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  state INT NOT NULL DEFAULT 0,
  regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  employee_code NVARCHAR(120) NULL,
  effective_from DATE NULL,
  effective_to DATE NULL,
  health_insurance NVARCHAR(120) NULL,
  health_last_four NVARCHAR(120) NULL,
  pension NVARCHAR(120) NULL,
  employment_insurance NVARCHAR(120) NULL,
  employment_last_four NVARCHAR(120) NULL,
  retirement_book NVARCHAR(120) NULL,
  confirmed_date DATE NULL,
  remarks NVARCHAR(1000) NULL,
  employee_id INT NULL,
  employee_name NVARCHAR(255) NULL,
  FOREIGN KEY (employee_id) REFERENCES dbo.employees(employee_id)
 );
END;
IF OBJECT_ID('dbo.operation_labor_log','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_labor_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
  id BIGINT NULL,
  version INT NULL,
  state INT NULL,
  regist_date DATETIME2 NULL,
  update_date DATETIME2 NULL,
  employee_code NVARCHAR(120) NULL,
  effective_from DATE NULL,
  effective_to DATE NULL,
  health_insurance NVARCHAR(120) NULL,
  health_last_four NVARCHAR(120) NULL,
  pension NVARCHAR(120) NULL,
  employment_insurance NVARCHAR(120) NULL,
  employment_last_four NVARCHAR(120) NULL,
  retirement_book NVARCHAR(120) NULL,
  confirmed_date DATE NULL,
  remarks NVARCHAR(1000) NULL,
  employee_id INT NULL,
  employee_name NVARCHAR(255) NULL,
  editor NVARCHAR(255) NOT NULL,
  process VARCHAR(20) NOT NULL,
  log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
 CREATE INDEX IX_operation_labor_log ON dbo.operation_labor_log(id,version);
END;
IF OBJECT_ID('dbo.operation_health','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_health (
  id BIGINT IDENTITY(1,1) PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  state INT NOT NULL DEFAULT 0,
  regist_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  update_date DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
  employee_code NVARCHAR(120) NULL,
  exam_date DATE NULL,
  exam_type NVARCHAR(120) NULL,
  next_date DATE NULL,
  systolic INT NULL,
  diastolic INT NULL,
  remarks NVARCHAR(1000) NULL,
  employee_id INT NULL,
  employee_name NVARCHAR(255) NULL,
  FOREIGN KEY (employee_id) REFERENCES dbo.employees(employee_id)
 );
END;
IF OBJECT_ID('dbo.operation_health_log','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_health_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY,
  id BIGINT NULL,
  version INT NULL,
  state INT NULL,
  regist_date DATETIME2 NULL,
  update_date DATETIME2 NULL,
  employee_code NVARCHAR(120) NULL,
  exam_date DATE NULL,
  exam_type NVARCHAR(120) NULL,
  next_date DATE NULL,
  systolic INT NULL,
  diastolic INT NULL,
  remarks NVARCHAR(1000) NULL,
  employee_id INT NULL,
  employee_name NVARCHAR(255) NULL,
  editor NVARCHAR(255) NOT NULL,
  process VARCHAR(20) NOT NULL,
  log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
 CREATE INDEX IX_operation_health_log ON dbo.operation_health_log(id,version);
END;
IF OBJECT_ID('dbo.operation_crew_members','U') IS NULL
BEGIN
 CREATE TABLE dbo.operation_crew_members (
  crew_id BIGINT NOT NULL REFERENCES dbo.operation_crew(id), employee_id INT NOT NULL REFERENCES dbo.employees(employee_id),
  employee_code NVARCHAR(120) NOT NULL, employee_name NVARCHAR(255) NOT NULL, is_driver BIT NOT NULL,
  PRIMARY KEY(crew_id,employee_id)
 );
 CREATE UNIQUE INDEX UX_operation_crew_driver ON dbo.operation_crew_members(crew_id) WHERE is_driver=1;
END;
IF OBJECT_ID('dbo.operation_crew_members_log','U') IS NULL
 CREATE TABLE dbo.operation_crew_members_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY, crew_id BIGINT NOT NULL, employee_id INT NOT NULL,
  employee_code NVARCHAR(120) NOT NULL, employee_name NVARCHAR(255) NOT NULL, is_driver BIT NOT NULL,
  version INT NOT NULL, editor NVARCHAR(255) NOT NULL, process VARCHAR(20) NOT NULL, log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
IF OBJECT_ID('dbo.operation_order_plans','U') IS NULL
 CREATE TABLE dbo.operation_order_plans (
  order_id INT PRIMARY KEY REFERENCES dbo.orders(order_id), version INT NOT NULL DEFAULT 0,
  leader_id INT NULL REFERENCES dbo.employees(employee_id), leader_name NVARCHAR(255) NULL
 );
IF OBJECT_ID('dbo.operation_order_vehicles','U') IS NULL
 CREATE TABLE dbo.operation_order_vehicles (
  order_id INT NOT NULL REFERENCES dbo.orders(order_id), crew_id BIGINT NOT NULL REFERENCES dbo.operation_crew(id),
  vehicle_code NVARCHAR(120) NOT NULL, vehicle_name NVARCHAR(120) NOT NULL, work_date DATE NOT NULL,
  PRIMARY KEY(order_id,crew_id)
 );
IF OBJECT_ID('dbo.operation_order_members','U') IS NULL
 CREATE TABLE dbo.operation_order_members (
  order_id INT NOT NULL, crew_id BIGINT NOT NULL, employee_id INT NOT NULL REFERENCES dbo.employees(employee_id),
  employee_code NVARCHAR(120) NOT NULL, employee_name NVARCHAR(255) NOT NULL, is_driver BIT NOT NULL,
  PRIMARY KEY(order_id,crew_id,employee_id),
  FOREIGN KEY(order_id,crew_id) REFERENCES dbo.operation_order_vehicles(order_id,crew_id)
 );
IF OBJECT_ID('dbo.operation_order_plans_log','U') IS NULL
 CREATE TABLE dbo.operation_order_plans_log (
  log_id BIGINT IDENTITY(1,1) PRIMARY KEY, order_id INT NOT NULL, version INT NOT NULL,
  leader_id INT NULL, leader_name NVARCHAR(255) NULL, vehicles_json NVARCHAR(MAX) NOT NULL, members_json NVARCHAR(MAX) NOT NULL,
  editor NVARCHAR(255) NOT NULL, process VARCHAR(20) NOT NULL, log_date DATETIME2 NOT NULL DEFAULT SYSDATETIME()
 );
COMMIT;
