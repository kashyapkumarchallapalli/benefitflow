-- Hibernate fetches IDs in blocks of 50 (allocationSize default)
CREATE SEQUENCE members_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE members (
                         id             BIGINT                   NOT NULL,
                         member_id      VARCHAR(20)              NOT NULL,
                         full_name      VARCHAR(100)             NOT NULL,
                         email          VARCHAR(255)             NOT NULL,
                         date_of_birth  DATE                     NOT NULL,
                         status         VARCHAR(20)              NOT NULL,
                         created_at     TIMESTAMP WITH TIME ZONE NOT NULL,

                         CONSTRAINT pk_members           PRIMARY KEY (id),
                         CONSTRAINT uq_members_member_id UNIQUE (member_id),
                         CONSTRAINT uq_members_email     UNIQUE (email),
                         CONSTRAINT ck_members_status    CHECK (status IN ('ACTIVE', 'INACTIVE'))
);