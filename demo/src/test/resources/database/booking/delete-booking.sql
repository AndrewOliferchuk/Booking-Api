delete from bookings
where user_id = 1
and accommodation_id = 1
and check_in_date = '2024-09-15'
and check_out_date = '2024-09-20'
and status = 'PENDING'
and id = 1;