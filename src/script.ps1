# Get all network adapters
$adapters = Get-NetAdapter

# Loop through each adapter
foreach ($adapter in $adapters)
{
    # Get IP addresses for the adapter
    $ipAddresses = Get-NetIPAddress -InterfaceAlias $adapter.Name

    # Display adapter name and IP addresses
    Write-Host "Adapter Name: $($adapter.Name)"
    foreach ($ipAddress in $ipAddresses)
    {
        # Determine IP address type (IPv4 or IPv6) and print accordingly
        if ($ipAddress.AddressFamily -eq "IPv4")
        {
            Write-Host "IPv4 Address: $($ipAddress.IPAddress)"
        }
        elseif ($ipAddress.AddressFamily -eq "IPv6")
        {
            Write-Host "IPv6 Address: $($ipAddress.IPAddress)"
        }
        else
        {
            Write-Host "Unknown Address Family: $($ipAddress.IPAddress)"
        }
    }
}
