local userId = ARGV[1]
local ticketId = ARGV[2]
local count = tonumber(ARGV[3])
local orderKey = "order:" .. userId .. ":ticket:" .. ticketId
local stockKey = "stock:ticket:" .. ticketId
local stock = tonumber(redis.call("get", stockKey)) ;

-- 1.判断购买票的库存是否充足,<0或许小于用户购买的数量 return 1
if stock < count or stock <= 0 then
    return 1
end

--2.判断用户order历史中该票的数量有没有超过4 超过就return 2
local ticketCount = tonumber(redis.call("hget", orderKey, ticketId)) or 0
if ticketCount >= 4 or ticketCount + count > 4 then
    return 2

end

--3.扣减库存
redis.call("INCRBY", stockKey, -count)
--4用户订单数量加1
redis.call("hincrby", orderKey, ticketId, count)
return 0


