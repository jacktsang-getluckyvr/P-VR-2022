
def call() {
			withCredentials([usernamePassword(credentialsId: 'gitcred', passwordVariable: 'github_token', usernameVariable: 'github_user')]){
				powershell """
				function gitHubReqHeaders([string]\$username, [string]\$token){
					[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
					\$base64AuthInfo = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes(("{0}:{1}" -f \$username,\$token)))
					\$headers = @{Authorization="Basic \$base64AuthInfo"}
					return \$headers
				}
				\$usrRolePair = @{ 
				"tatsuyakosuda-getluckyvr" = "Graphics"
				"davidlaurin-getluckyvr" = "Tech Art"
				"han-luckyvr" = "Core"
				"joemeiners-getluckyvr" = "Sound"
				"Aushton" = "Props"
				"JamesBLuckyVR" = "Casino"
				"MaxMamyshev-getluckyvr" = "Casino"
				"rikardakesson-getluckyvr" = "Live Ops"
				"songqiaoluckyvr" = "Core"
				"Luango" = "Programmer"
				"Kolton-LuckyVR" = "Casino"
				"IanMcCabe" = "Tech Art"
				"scottedsall" = "Slots"
				"davidwigmore-lucky" = "Animator"
				"timma-getluckyvr" = "Core"
				"icormier" = "Tech Art"
				"kannieric" = "3D Art"
				"samuel-getluckyvr" = "Game Programmer"
				"pauleliasov-getluckyvr" = "BE/FE Programmer"
				"natlh" = "UI Designer"
				"DiegoDCamacho" = "Sports Betting: Backend Prog"
				"maurigalvez91" = "Slots Prog"
				"MathewGetluckyvr" = "Env Artist"
				"dustin-lucky" = "Casino Prog"
				"minsoo-getluckyvr" = "Player Suite Prog"
				"erdemgunay-getluckyvr" = "Club Dev"
				"getluckyvrivan" = "FE Programmer"
				"jacktsang-getluckyvr" = "Build Engineer"
				"AltamashVR" = "Slots"
				"LuckyVRMD" = "Tech Art"
				"lokisharma-getluckyvr" = "Unity Architect"
				"NicolasLuckyVR" = "Game Prog, Eye/Face Tracking"
				"qrustonvr" = "Live Ops Dev"
				"surajsirohi" = "UI Design"
				"Z26Liu" = "Player Suite"
				"DershZ" = "Architech"
				"maximebrindamour-getluckyvr" = "Slots Dev"
				"ryanblanchard-getluckyvr" = "VFX Artist"
				"JasonPercival-getluckyvr" = "Backend App Engineer"
				"Christian-LuckyVR" = "Core Dev"
				"kevintieu-getluckyvr" = "DevOps"
				}
				\$headers = gitHubReqHeaders -token \$ENV:github_token
				\$html="<Html>
				<Body>
					<Table border=1 style='border-collapse: collapse;text-align:center'>
					<tr>
						<td colspan=3 style='text-align:center'><font size='+2'><b>MERGED PRs INCLUDED IN THIS BUILD FROM BRANCH: ${params.Branch}</b></font></td>
					</tr>
					<th>PR Title</th>
					<th>PR Owner</th>
					<th>JIRAs</th>
"
				\$prData = @()
				for (\$i =1; \$i -le 10; \$i++){
					\$url = "https://api.github.com/search/issues?q=repo:LuckyVR/P-VR-2022+is:merged%20is:pr%20base:${params.Branch}&per_page=100&page=\$i"					
					Invoke-RestMethod -Method Get -Uri \$url -Headers \$headers -OutFile 'githubFilter_${env.BUILD_NUMBER}.json'
					\$prData = Get-Content 'githubFilter_${env.BUILD_NUMBER}.json' | Out-String | ConvertFrom-Json
					foreach (\$PR in \$prData.items){ 
						\$jiraList = ""
						\$regex = [regex] 'LUC-\\d+'
						\$matches = \$regex.Matches(\$PR.title + \$PR.body)
						\$uniMatches = \$matches.Value | Select-Object -Unique
						foreach (\$match in \$uniMatches){
							\$jiraList += ", <a href='https://luckyvr.atlassian.net/browse/\$match)'>\$match</a>"
						}
						while (\$jiraList.StartsWith(",")){
							\$jiraList = \$jiraList.Substring(1)
						}
						\$usrSearch = \$(\$PR.user.login)
						\$usrMatchRole = \$usrRolePair.GetEnumerator() | Where-Object { \$_.Name.Split(',')[0] -eq \$usrSearch} | ForEach-Object {\$_.Name + " (" + \$_.Value + ")"}
						if (\$usrMatchRole){
							\$usrMatchRoleStr = \$usrMatchRole -join ", "	
						}else {    
							\$usrMatchRoleStr = \$(\$PR.user.login)
						}
						\$html+= "<tr>
						<td><a href='\$(\$PR.html_url)'>\$(\$PR.title)</a></td>
						<td>\$usrMatchRoleStr</td>
						<td>\$jiraList</td>
						</tr>"
					}
				}
				\$html >> pr_report_${env.BUILD_NUMBER}.html
				\$html +="</table></body></html>"
				Remove-Item 'githubFilter_${env.BUILD_NUMBER}.json'
				"""    	
			}
}
