local userId = ARGV[1]
local ticketId = ARGV[2]
local count = tonumber(ARGV[3])
local performanceId = ARGV[4]

local stockKey = "stock:ticket:" .. ticketId
local pendingHash = "order:pending:" .. userId
local payedHash = "order:pay:" .. userId

-- 1. 库存检查
local stock = tonumber(redis.call("GET", stockKey))
if not stock or stock < count then
    return 1
end

-- 2. 计算已持有总数（pending + payed）
local pending = tonumber(redis.call("HGET", pendingHash, performanceId)) or 0
local payed = tonumber(redis.call("HGET", payedHash,performanceId)) or 0
local total = pending + payed + count

if total > 4 then
    return 2  -- 超过限购
end

-- 3. 扣库存
redis.call("INCRBY", stockKey, -count)

-- 4. 增加待支付
redis.call("HINCRBY", pendingHash, performanceId, count)

-- 5. 设置 30 分钟过期
redis.call("EXPIRE", pendingHash, 1800)

return 0