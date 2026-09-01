/*
 * 勤怠打刻テーブル再構築（SQL Server）
 * 既存データを残す場合は、実行前に timeworks / timeworks_edit を退避してください。
 */
IF OBJECT_ID(N'dbo.timeworks_edit', N'U') IS NOT NULL
    DROP TABLE dbo.timeworks_edit;

IF OBJECT_ID(N'dbo.timeworks', N'U') IS NOT NULL
    DROP TABLE dbo.timeworks;

CREATE TABLE dbo.timeworks (
    timework_id BIGINT IDENTITY(1, 1) NOT NULL,
    employee_id BIGINT NOT NULL,
    office_id BIGINT NULL,
    work_date DATE NOT NULL,
    start_time DATETIME2 NULL,
    end_time DATETIME2 NULL,
    regist_date DATETIME2 NOT NULL CONSTRAINT DF_timeworks_regist_date DEFAULT SYSDATETIME(),
    update_date DATETIME2 NOT NULL CONSTRAINT DF_timeworks_update_date DEFAULT SYSDATETIME(),
    regist_user NVARCHAR(255) NOT NULL CONSTRAINT DF_timeworks_regist_user DEFAULT N'system',
    update_user NVARCHAR(255) NOT NULL CONSTRAINT DF_timeworks_update_user DEFAULT N'system',
    version INT NOT NULL CONSTRAINT DF_timeworks_version DEFAULT 1,
    state INT NOT NULL CONSTRAINT DF_timeworks_state DEFAULT 0,
    CONSTRAINT PK_timeworks PRIMARY KEY (timework_id),
    CONSTRAINT FK_timeworks_employee FOREIGN KEY (employee_id) REFERENCES dbo.employees(employee_id),
    CONSTRAINT FK_timeworks_office FOREIGN KEY (office_id) REFERENCES dbo.offices(office_id),
    CONSTRAINT CK_timeworks_stamp_order CHECK (end_time IS NULL OR (start_time IS NOT NULL AND end_time >= start_time))
);

CREATE UNIQUE INDEX UX_timeworks_employee_work_date_active
    ON dbo.timeworks(employee_id, work_date)
    WHERE state = 0;

CREATE INDEX IX_timeworks_work_date_office
    ON dbo.timeworks(work_date, office_id, state)
    INCLUDE(employee_id, start_time, end_time);
