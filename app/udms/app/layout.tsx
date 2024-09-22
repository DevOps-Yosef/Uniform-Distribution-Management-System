'use client'
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import { AppRouterCacheProvider } from "@mui/material-nextjs/v13-appRouter";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import CssBaseline from "@mui/material/CssBaseline";
import Divider from "@mui/material/Divider";
import Drawer from "@mui/material/Drawer";
import Grid from "@mui/material/Grid";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import LogoutMenu from "./components/logout-menu";
import SidebarLinks from "./components/sidebar-links";
import "./globals.css";
import "./udms-styles.css";


const drawerWidth = 240;

export default async function RootLayout(props) {
  const { children } = props;

  return (
    <html lang="en">
      <body>
        <AppRouterCacheProvider>
          return (
          <Box sx={{ display: "flex" }}>
            <CssBaseline />
            <AppBar position="fixed" sx={{ zIndex: 1201 }} className="udms-header-bg-color">
              <Toolbar>
                <Grid container justifyContent="flex-start">
                  <img src="/UDMS/_next/static/media/logo.png" alt="ZB Logo" style={{ border: "solid red 0px", borderRadius: "10px 5%" }} />
                  <Divider
                    orientation="vertical"
                    variant="middle"
                    flexItem className="udms-header-separator-style"
                    sx={{ marginLeft: 1 }}
                  />
                  <Typography
                    variant="h6"
                    sx={{ flexGrow: 1 }}
                    noWrap
                    ml={1}
                    className="udms-main-text-color"
                  >
                    Uniform Distribution Management System
                  </Typography>
                  <LogoutMenu />
                </Grid>
              </Toolbar>
            </AppBar>

            <Drawer
              variant="permanent"
              sx={{
                width: drawerWidth,
                flexShrink: 0,
                [`& .MuiDrawer-paper`]: {
                  width: drawerWidth,
                  boxSizing: "border-box",
                },
              }}
            >
              <Toolbar />
              <Box sx={{ paddingTop: 3 }}>
                <SidebarLinks />
              </Box>

            </Drawer>

            <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
              <Toolbar />
              {children}
            </Box>
          </Box>
          );
        </AppRouterCacheProvider>
      </body>
    </html >
  );
}
