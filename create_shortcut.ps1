$desktop = [Environment]::GetFolderPath('Desktop')
$shell = New-Object -ComObject WScript.Shell
$shortcutPath = Join-Path $desktop 'QuantCalc (QUANTCALC).lnk'
$target = if (Test-Path "$env:ProgramFiles\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javaw.exe") {"$env:ProgramFiles\Eclipse Adoptium\jdk-25.0.3.9-hotspot\bin\javaw.exe"} else {"javaw"}
$shortcut = $shell.CreateShortcut($shortcutPath)
$shortcut.TargetPath = $target
$shortcut.Arguments = '-cp "' + (Split-Path -Path $MyInvocation.MyCommand.Definition -Parent) + '" Calculator'
$shortcut.WorkingDirectory = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
$icon = Join-Path (Split-Path -Path $MyInvocation.MyCommand.Definition -Parent) 'quantcalc-icon.ico'
if (Test-Path $icon) { $shortcut.IconLocation = $icon }
$shortcut.Save()
Write-Output "Created shortcut: $shortcutPath"
