'use client'
import * as React from "react";
import Typography from "@mui/material/Typography";
import { refreshInactiveToken } from "../services/ServiceUtil";
import { useEffect } from "react";

export default function Page() {

  useEffect(() => {
    try {
      refreshInactiveToken().then(data => {
    });
    } catch (error) {
    }
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      await refreshInactiveToken();
    };
    fetchData();
  }, []);

  return (
    <div className="udms-container" >
      <div className="udms-header">
      <Typography variant="h4" component="div">Welcome to the Uniform Distribution Management System</Typography>
        <p>Efficiently manage your uniform inventory, orders, and distribution processes.</p>
      </div>

      <div className="udms-features">
        <div className="udms-feature-card">
        <Typography variant="h5" component="div">Inventory Management</Typography>
          <p>Keep track of uniform stock levels and receive alerts for low inventory.</p>
        </div>
        <div className="udms-feature-card">
        <Typography variant="h5" component="div">Order Processing</Typography>
          <p>Process orders quickly with automated workflows and real-time updates.</p>
        </div>
        <div className="udms-feature-card">
        <Typography variant="h5" component="div">Reporting Tools</Typography>
          <p>Generate insightful reports to make data-driven decisions.</p>
        </div>
        <div className="udms-feature-card">
        <Typography variant="h5" component="div">User Settings</Typography>
          <p>Customize your experience and manage account settings with ease.</p>
        </div>
      </div>
    </div>

  );
}
