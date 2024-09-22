'use client'
import { Alert, AlertTitle, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, Grid, LinearProgress, Paper, TableHead, TextField, Typography } from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import { styled } from '@mui/material/styles';
import { useEffect, useState } from 'react';
import { refreshInactiveToken, requestReports, requestResourceWithCredsPost } from "../../../services/ServiceUtil";
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';

export default function Page() {
  const [openAction, setOpenAction] = useState(false);
  const [openActionStatus, setOpenActionStatus] = useState(false);
  const [actionStatus, setActionStatus] = useState('');
  const [isForbidden, setIsForbidden] = useState(false);
  const [loadingReport, setLoadingReport] = useState(false);
  const [uniformMasterReport, setUniformMasterReport] = useState(null);
  const [dateFrom, setDateFrom] = useState<Dayjs | null>(null);
  const [dateTo, setDateTo] = useState<Dayjs | null>(null);

  // const redirectURIApi = "http://localhost:8082/udmsapi";
  const redirectURIApi = "https://aps3.zemenbank.com/TestUDMS/udmsapi";
  // const redirectURIApi = "https://aps3.zemenbank.com/UDMS/udmsapi";

  let apiUniformsCommonMasterReportDownload = redirectURIApi + "/uniforms/common/masterreportdownload";

  useEffect(() => {
    try {
      refreshInactiveToken().then(data => {
    });
    } catch (error) {
    }
  }, []);

  useEffect(() => {
    if (uniformMasterReport) {
      try {
        if (typeof window !== 'undefined') {
          const url = window.URL.createObjectURL(uniformMasterReport);
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', 'Uniform Master Report.xlsx');
          document.body.appendChild(link);
          link.click();
          window.URL.revokeObjectURL(url);
          link.remove();
          setLoadingReport(false);
        } else {
        }
      } catch (error) {
      }
    }
  }, [uniformMasterReport]);

  const handleActionStatusClose = () => {
    setOpenActionStatus(false);
  };

  const handleActionClose = () => {
    setOpenAction(false);
  };


  const handleDownload = () => {
    try {
      if (!dateFrom?.isValid && !dateTo?.isValid) {
        setActionStatus('form-fail');
        setOpenActionStatus(true);
        return;
      }
      setLoadingReport(true);
      const queryParams = {
        dateFrom: dateFrom,
        dateTo: dateTo,
      };
      requestReports(apiUniformsCommonMasterReportDownload, queryParams)
        .then(data => {
          const binaryData = Buffer.from(data.byteArr);
          const blob = new Blob([binaryData.buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          setUniformMasterReport(blob);
        }
        )
        .catch(error => {
          console.log(error);
        });
    } catch (error) {
    }
  };

  if (isForbidden) {
    return (
      <div>
        <Alert variant="outlined" severity="error">
          Access is forbidden.
        </Alert>
      </div>
    )
  }

  return (
    <>
      <div>
        <div>
          <Dialog open={openActionStatus} onClose={handleActionStatusClose}>
            <DialogTitle align='center'>Action Status</DialogTitle>
            <DialogContent>
              {actionStatus && actionStatus === 'form-fail' && <Alert severity="error">Select Date.</Alert>}
            </DialogContent>
          </Dialog>
        </div>
      </div>

      <Grid>
        <Grid container spacing={2} paddingTop={2}>
          <Grid item xs={3}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker name="dateFrom" label="Date From" value={dateFrom}
                onChange={(newValue) => setDateFrom(newValue)}/>
            </LocalizationProvider>
          </Grid>
          <Grid item xs={3}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker name="dateTo" label="Date To" value={dateTo}
                onChange={(newValue) => setDateTo(newValue)}/>
            </LocalizationProvider>
          </Grid>
          <Grid item xs={2}>
            <Button variant="contained" onClick={handleDownload} disabled={loadingReport}>
              {loadingReport ? 'Downloading...' : 'Get Report'}
            </Button>
          </Grid>
        </Grid>
      </Grid>
    </>
  );
}
