shortActionV1Table = {}




-- 构建翻前搜索是第一轮状态 + 第二轮状态
-- huuuuu 选择 a => huuuuu_a
-- 后位行动 caffah
-- 组合为 huuuuu_a_caffah
function buildShortV1Query(status1, action, status2)
    local query = table.concat(status1).."_"..action.."_"..table.concat(status2)
end

-- 读取 A8s:1.0,A9s:1.0,ATs:1.0,ATo:0.13,AJs:1.0,AJo:0.592,AQs:1.0,AQo:1.0,AKs:0.192,AKo:1.0,
-- 转化为Table: {A8s: 1.0, A9s: 1.0}
-- 所有值概率是总概率，如果需要条件概率，需要 huuu_c_aaa[1] / huuu_c[1] = huuu_c_aaa[1] / huuu[2]
function formatShortV1HandsRangeData(dataStr)
    local res = {}
    dataStr = string.lower(dataStr)
    -- 分隔符
    local tmpDataPairSplit = stringSplit(dataStr, ",")
    for _, handsDataPair in ipairs(tmpDataPairSplit) do
        local tmp = stringSplit(handsDataPair, ":")
        res[tmp[1]] = tonumber(tmp[2])
    end
    return res
end

--player act	status 2nd rnd
--utg play	huuuuu	a		A8s:1.0,A9s:1.0,ATs:1.0,ATo:0.13,AJs:1.0,AJo:0.592,AQs:1.0,AQo:1.0,AKs:0.192,AKo:1.0,T9s:0.806,TT:0.678,JTs:1.0,JTo:0.624,QTs:1.0,KTs:1.0,JJ:0.816,QJs:1.0,KJs:1.0,QQ:0.592,KQs:1.0,KQo:1.0,KK:0.528
--huuuuu	c		AA:1.0,A6s:0.216,A7s:1.0,A9o:0.272,ATo:0.87,AJo:0.408,AKs:0.808,98s:1.0,T8s:1.0,99:0.57,T9s:0.194,T9o:0.036,J9s:0.536,TT:0.322,JTo:0.376,QTo:0.524,KTo:0.212,JJ:0.184,QJo:0.806,KJo:0.886,QQ:0.408,KK:0.472
--huuuuu	c	acfff	AA:1.0,ATo:0.688,AJo:0.408,AKs:0.808,TT:0.322,JJ:0.184,QQ:0.408,KK:0.472

-- 生成结果 {query: handsTable}
-- handsTable[1]: p(a) handsTable[2]: p（c） handsTable[3]: p（f）
function formatShortV1PreflopData(dataFileName, short)
    local file = io.open(localPath..dataFileName, "r")
    if (file == nil) then
        return false
    end

    local query1 = ""

    -- 胜率 负率 平率
    for line in file:lines() do
        if line ~= nil then
            -- "%s" stands for whitespace in lua
            line = string.lower(line)
            -- 分隔符

            local tmpFileSplit = stringSplit(line, "%s")

            local query2 = ""
            local query1 = ""
            local act1 = ""

            if tmpFileSplit[2] ~= nil and tmpFileSplit[3] ~= nil then
                query1 = tmpFileSplit[2]
                act1 = tmpFileSplit[3]
                local rangeData = tmpFileSplit[5]

                --print("4: "..tmpFileSplit[4])
                if tmpFileSplit[4] == "n"  then
                    -- 有效输入:第一轮行动表
                    -- 更新表
                    local action = tmpFileSplit[3]

                    local rangePairMap = formatShortV1HandsRangeData(rangeData)
                    if shortActionV1Table[query1] == nil then
                        shortActionV1Table[query1] = {}
                    end
                    for hands, value in pairs(rangePairMap) do
                        if shortActionV1Table[query1][hands] == nil then
                            shortActionV1Table[query1][hands] = {0,0,0}
                        end

                        if action == "a" or string.sub(action,1,1) == "r" or action == "ca" then
                            shortActionV1Table[query1][hands][1] = shortActionV1Table[query1][hands][1] + value
                        elseif action == "c" then
                            shortActionV1Table[query1][hands][2] = shortActionV1Table[query1][hands][2] + value
                        end

                        if shortActionV1Table[query1][hands][1] + shortActionV1Table[query1][hands][2] > 1.05 then
                            errorLog(string.format("data err: %s_%s, a: %f, c: %f",query1,hands,shortActionV1Table[query1][hands][1],shortActionV1Table[query1][hands][2]))
                        end
                    end
                else
                    --debugLog(line)
                    -- 有效输入：第二轮行动表
                    -- 更新表
                    -- 第二轮意味着有人raise， 因此返回的act都是raise
                    query2 = tmpFileSplit[4]
                    local query = query1.."_"..act1.."_"..query2
                    local rangePairMap = formatShortV1HandsRangeData(rangeData)

                    --print("query "..query)
                    shortActionV1Table[query] = {}
                    for hands, value in pairs(rangePairMap) do
                        shortActionV1Table[query][hands] = {value,0,1-value}
                    end
                end
            end
        end
    end

    for query, QueryHandsData in pairs(shortActionV1Table) do
        for _, handsData in pairs(QueryHandsData) do
            handsData[3] = 1-handsData[1]-handsData[2]
            if handsData[3] < 0.001 then
                handsData[3] = 0
            end
        end
    end

    io.close(file)
    return true
end
