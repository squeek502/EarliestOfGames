local DEBUG = false
local DRY_RUN = false

local function trim(s)
  return s:match'^()%s*$' and '' or s:match'^%s*(.*%S)'
end

local function printdebug(title, data)
	title = tostring(title)
	print(title.."-->\n"..tostring(data).."\n<--"..title.."\n")
end

local function commit(message_title)
	-- add any untracked files
	os.execute('git add -A')
	
	-- get status
	local statusoutput = io.popen('git status --short')
	local status = trim(statusoutput:read("*a"))
	statusoutput:close()
	
	-- if there are changes to commit
	if status ~= "" then
		print("Committing...\n")
		
		message_title = message_title.." ("..os.date()..")"
		local message_body = status
		
		if DEBUG then
			printdebug("Status", status)
			printdebug("Message", message_title.."\n\n"..message_body)
		else
			print(message_title)
		end
		
		-- commit
		os.execute('git commit -m "'..message_title..'" -m "'..message_body..'"'..(DRY_RUN and " --dry-run" or ""))
		
		-- push
		os.execute('git push origin master'..(DRY_RUN and " --dry-run" or ""))
	else
		print("No changes")
	end
end

local interval = 15 -- minutes
while true do
	commit(interval.." minute autocommit")
	print("\nWaiting "..interval.." minutes...")
	os.execute("sleep "..interval*60)
end