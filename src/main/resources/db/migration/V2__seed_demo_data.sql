INSERT INTO users (id, full_name, email, password_hash, role, status, must_change_password, last_login_at, created_at, created_by, updated_at, updated_by)
VALUES
    (1, 'Amina Rahman', 'admin@pipelinex.local', '$2a$12$XE7LKBkBq706NqUBnEoZL.6vnQzcP2BPf/TRqMaC0TY2.wlGIaS9O', 'ADMIN', 'ACTIVE', FALSE, NULL, NOW(), NULL, NOW(), NULL),
    (2, 'Noah Carter', 'rep.noah@pipelinex.local', '$2a$12$XE7LKBkBq706NqUBnEoZL.6vnQzcP2BPf/TRqMaC0TY2.wlGIaS9O', 'REP', 'ACTIVE', TRUE, NULL, NOW(), 1, NOW(), 1),
    (3, 'Layla Chen', 'rep.layla@pipelinex.local', '$2a$12$XE7LKBkBq706NqUBnEoZL.6vnQzcP2BPf/TRqMaC0TY2.wlGIaS9O', 'REP', 'ACTIVE', TRUE, NULL, NOW(), 1, NOW(), 1),
    (4, 'Omar Diaz', 'rep.inactive@pipelinex.local', '$2a$12$XE7LKBkBq706NqUBnEoZL.6vnQzcP2BPf/TRqMaC0TY2.wlGIaS9O', 'REP', 'INACTIVE', TRUE, NULL, NOW(), 1, NOW(), 1);

SELECT setval('users_id_seq', 4, true);

INSERT INTO leads (id, lead_name, company_name, phone, email, lead_source, company_website, job_title, linkedin_url, notes, current_stage, assigned_to_user_id, assigned_at, archived, next_follow_up_date, next_follow_up_status, last_activity_at, created_at, created_by, updated_at, updated_by)
VALUES
    (1, 'Morgan Blake', 'Northstar Systems', '+15551001', 'morgan@northstar.example', 'Conference', 'https://northstar.example', 'Operations Director', 'https://linkedin.com/in/morgan-blake', 'Warm inbound conversation from expo.', 'QUALIFIED', 2, NOW() - INTERVAL '5 days', FALSE, CURRENT_DATE + 1, 'PENDING', CURRENT_DATE - 1, NOW() - INTERVAL '7 days', 1, NOW() - INTERVAL '1 day', 2),
    (2, 'Elena Park', 'Copper Ridge Labs', '+15551002', 'elena@copperridge.example', 'Referral', 'https://copperridge.example', 'COO', 'https://linkedin.com/in/elena-park', 'Interested in quarterly rollout.', 'PROPOSAL', 3, NOW() - INTERVAL '9 days', FALSE, CURRENT_DATE - 2, 'PENDING', CURRENT_DATE - 3, NOW() - INTERVAL '9 days', 1, NOW() - INTERVAL '2 days', 3),
    (3, 'Samir Holt', 'Beacon Freight', '+15551003', 'samir@beaconfreight.example', 'Outbound', 'https://beaconfreight.example', 'VP Sales', 'https://linkedin.com/in/samir-holt', 'Needs tighter follow-up cadence.', 'CONTACTED', 2, NOW() - INTERVAL '3 days', FALSE, CURRENT_DATE + 4, 'PENDING', CURRENT_DATE, NOW() - INTERVAL '3 days', 1, NOW(), 2),
    (4, 'Rina Patel', 'Atlas Grid', '+15551004', 'rina@atlasgrid.example', 'Partner', 'https://atlasgrid.example', 'Head of Revenue', 'https://linkedin.com/in/rina-patel', 'Still evaluating budget.', 'NEW', NULL, NULL, FALSE, NULL, NULL, NULL, NOW() - INTERVAL '1 day', 1, NOW() - INTERVAL '1 day', 1),
    (5, 'Jon Reeves', 'Meridian Steel', '+15551005', 'jon@meridiansteel.example', 'Conference', 'https://meridiansteel.example', 'Founder', 'https://linkedin.com/in/jon-reeves', 'Closed successfully last week.', 'WON', 3, NOW() - INTERVAL '20 days', FALSE, NULL, NULL, CURRENT_DATE - 7, NOW() - INTERVAL '21 days', 1, NOW() - INTERVAL '7 days', 3);

SELECT setval('leads_id_seq', 5, true);

INSERT INTO lead_assignment_history (lead_id, previous_rep_id, new_rep_id, changed_by, note, changed_at)
VALUES
    (1, NULL, 2, 1, 'Initial assignment from admin.', NOW() - INTERVAL '5 days'),
    (2, NULL, 3, 1, 'Initial assignment from admin.', NOW() - INTERVAL '9 days'),
    (3, NULL, 2, 1, 'Outbound queue assignment.', NOW() - INTERVAL '3 days'),
    (5, NULL, 3, 1, 'Assigned during qualification.', NOW() - INTERVAL '20 days');

INSERT INTO lead_stage_history (lead_id, previous_stage, new_stage, changed_by, changed_at)
VALUES
    (1, 'NEW', 'CONTACTED', 2, NOW() - INTERVAL '4 days'),
    (1, 'CONTACTED', 'QUALIFIED', 2, NOW() - INTERVAL '2 days'),
    (2, 'NEW', 'CONTACTED', 3, NOW() - INTERVAL '8 days'),
    (2, 'CONTACTED', 'QUALIFIED', 3, NOW() - INTERVAL '5 days'),
    (2, 'QUALIFIED', 'PROPOSAL', 3, NOW() - INTERVAL '2 days'),
    (5, 'PROPOSAL', 'WON', 3, NOW() - INTERVAL '7 days');

INSERT INTO lead_activity (lead_id, activity_type, activity_date, notes, logged_by, created_at)
VALUES
    (1, 'CALL', CURRENT_DATE - 3, 'Discussed implementation needs.', 2, NOW() - INTERVAL '3 days'),
    (1, 'EMAIL', CURRENT_DATE - 1, 'Shared pricing deck.', 2, NOW() - INTERVAL '1 day'),
    (2, 'MEETING', CURRENT_DATE - 4, 'Proposal walk-through.', 3, NOW() - INTERVAL '4 days'),
    (3, 'EMAIL', CURRENT_DATE, 'Initial outreach and case study.', 2, NOW()),
    (5, 'CALL', CURRENT_DATE - 7, 'Confirmed signed agreement.', 3, NOW() - INTERVAL '7 days');

INSERT INTO lead_follow_up (lead_id, assigned_rep_id, due_date, status, scheduled_by, completed_by, completed_at, completion_note, created_at, updated_at)
VALUES
    (1, 2, CURRENT_DATE + 1, 'PENDING', 2, NULL, NULL, NULL, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
    (2, 3, CURRENT_DATE - 2, 'PENDING', 3, NULL, NULL, NULL, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
    (3, 2, CURRENT_DATE + 4, 'PENDING', 2, NULL, NULL, NULL, NOW(), NOW()),
    (5, 3, CURRENT_DATE - 7, 'COMPLETED', 3, 3, NOW() - INTERVAL '7 days', 'Auto-closed after deal was won.', NOW() - INTERVAL '8 days', NOW() - INTERVAL '7 days');
