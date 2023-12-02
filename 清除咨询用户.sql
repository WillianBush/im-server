delete from member where nickName like '%咨询用户%';
delete from member   where exists  ( select * from( select memberId  from member GROUP BY memberId HAVING count(1)>1) t where t.memberId=memberId);
delete from accessrecord  where not EXISTS (select 1 from member b where b.id=uid);

delete from friends  where not EXISTS (select 1 from member b where b.id=mid);

delete from friendsadd  where not EXISTS (select 1 from member b where b.id=mid);

delete from memberloginlog  where not EXISTS (select 1 from member b where b.memberId=mid);

delete from message_history  where not EXISTS (select 1 from member b where b.id=toUid or b.id=fromUid );

delete from room_member  where not EXISTS (select 1 from member b where b.id=member_id);

delete from roomadd  where not EXISTS (select 1 from member b where b.id=mid);

delete from waitsendmessage  where not EXISTS (select 1 from member b where b.id=toUid or b.id=fromUid );