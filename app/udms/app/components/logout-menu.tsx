"use client";
import * as React from "react";
import { styled } from "@mui/material/styles";
import { IconButton, Typography } from "@mui/material/";
import InputBase from "@mui/material/InputBase";
import { MenuItem, Menu, ListItemIcon, Link } from "@mui/material/";
import { pink } from "@mui/material/colors";
import { Home, Settings, Book, AccountBox, AccountBoxRounded, Logout } from "@mui/icons-material";
import { useEffect, useState } from "react";
import { getUserDetailsTwo } from "../services/ServiceUtil";

const StyledInputBase = styled(InputBase)(({ theme }) => ({
  color: "inherit",
  "& .MuiInputBase-input": {
    padding: theme.spacing(1, 1, 1, 0),
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create("width"),
    width: "100%",
    [theme.breakpoints.up("md")]: {
      width: "20ch",
    },
  },
}));

// const homeLink = '/';
const homeLink = '/TestUDMS/';
// const homeLink = '/UDMS/';

export default function LogoutMenu() {

  const [userName, setUserName] = useState("");

  useEffect(() => {
    const fetchData = async () => {
      getUserDetailsTwo().then(userName => {
        if (userName) {
          setUserName(userName);
        }
      });
    };
    fetchData();
  }, []);


  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const isMenuOpen = Boolean(anchorEl);

  const handleProfileMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleLogoutLinkClick = () => {
    setAnchorEl(null);
    // deleteCookie();
  };

  const menuId = "primary-search-account-menu";
  const renderMenu = (
    <Menu
      anchorEl={anchorEl}
      anchorOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      id={menuId}
      keepMounted
      transformOrigin={{
        vertical: "top",
        horizontal: "right",
      }}
      open={isMenuOpen}
      onClose={handleMenuClose}
    >
      <MenuItem component={Link} href="https://auth.zemenbank.com/auth/realms/test/account" target="_blank" rel="noopener noreferrer">
        <AccountBox fontSize="small" style={{ marginRight: 8 }} />
        SSO Profile
      </MenuItem>
      <MenuItem component={Link} href="https://aps3.zemenbank.com/UDMS/_next/static/media/Uniform Distribution Management System User Guide.pdf" target="_blank" rel="noopener noreferrer">
        <Book fontSize="small" style={{ marginRight: 8 }} />
        User Guide
      </MenuItem>
      <MenuItem component={Link} href={homeLink + "logout"} onClick={handleLogoutLinkClick}>
        <Logout fontSize="small" style={{ marginRight: 8 }} />
        Logout
      </MenuItem>

    </Menu>
  );

  return (
    <div>
      <IconButton
        size="large"
        edge="end"
        onClick={handleProfileMenuOpen}
        color="inherit"
      >
        <Typography className="udms-main-text-color">{userName}
        </Typography>
        <AccountBoxRounded sx={{ color: pink[500] }} fontSize="inherit" />
      </IconButton>
      {renderMenu}
    </div>
  );
}
