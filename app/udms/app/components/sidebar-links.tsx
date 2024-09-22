"use client";
import { Box, Divider, Grid, Link, List, ListItem, ListItemButton, ListItemText } from "@mui/material/";
import { useEffect, useState } from "react";
import { getUserDetailsOne } from "../services/ServiceUtil";

export default function SidebarLinks() {

  // const homeLink = '/';
  const homeLink = '/TestUDMS/';
  // const homeLink = '/UDMS/';

  const [roleName, setRoleName] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      getUserDetailsOne().then(roleResponse => {
        if (roleResponse) {
          setRoleName(roleResponse);
        }
      });
    };
    fetchData();
  }, []);


  return (
    <div>
      <Box sx={{ overflow: "auto" }}>
        {roleName === "Officer" && (
          <Grid>
            <List>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "employees"}>
                <ListItemText primary="Entitled Staff" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "positions"}>
                <ListItemText primary="Entitled Positions" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/issue"}>
                <ListItemText primary="Uniform Issue" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/archive"}>
                <ListItemText primary="Request Archive" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/issue/archive"}>
                <ListItemText primary="Issue Archive" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/report/master"}>
                <ListItemText primary="Master Report" />
              </ListItemButton>
            </List>
          </Grid>
        )}
        {roleName === "Office Manager" && (
          <Grid>
            <List>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "positions/approval"}>
                <ListItemText primary="Approve Entitlements" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/issue/approval"}>
                <ListItemText primary="Approve Issue" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/approval"}>
                <ListItemText primary="Approve Request" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/archive"}>
                <ListItemText primary="Request Archive" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/issue/archive"}>
                <ListItemText primary="Issue Archive" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/report/master"}>
                <ListItemText primary="Master Report" />
              </ListItemButton>
            </List>
          </Grid>
        )}
        {roleName === "CSR" && (
          <Grid>
            <List>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms"}>
                <ListItemText primary="Request" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/archive"}>
                <ListItemText primary="Request Archive" />
              </ListItemButton>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/issue/archive"}>
                <ListItemText primary="Issue Archive" />
              </ListItemButton>
            </List>
          </Grid>
        )}
        {roleName === "Manager" && (
          <Grid>
            <List>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "uniforms/approval"}>
                <ListItemText primary="Approve Request" />
              </ListItemButton>
            </List>
          </Grid>
        )}
        {roleName === "Admin" && (
          <Grid>
            <List>
              <Divider />
              <ListItemButton component={Link} href={homeLink + "admin"}>
                <ListItemText primary="Access Log" />
              </ListItemButton>
            </List>
          </Grid>
        )}
      </Box>
    </div>
  );
}
