'use client'
import { Search } from '@mui/icons-material';
import { Alert, AlertTitle, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, Grid, InputLabel, LinearProgress, MenuItem, OutlinedInput, Paper, Select, SelectChangeEvent, TableHead, TextField, Typography } from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import { styled } from '@mui/material/styles';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { useFormState, useFormStatus } from 'react-dom';
import { requestResourceWithCredsPost } from "../../services/ServiceUtil";
import { refreshInactiveToken } from "../../services/ServiceUtil";

export default function Page() {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(4);
  const [rows, setRows] = useState<Entitlement[]>([]);
  const [open, setOpen] = useState(false);
  const [openAction, setOpenAction] = useState(false);
  const [openActionStatus, setOpenActionStatus] = useState(false);
  const [actionStatus, setActionStatus] = useState(false);
  const [selectedPositionCategory, setSelectedPositionCategory] = useState('');
  const [selectedEntitlement, setSelectedEntitlement] = useState<Entitlement>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isForbidden, setIsForbidden] = useState(false);

  // const redirectURIApi = "http://localhost:8082/udmsapi";
  const redirectURIApi = "https://aps3.zemenbank.com/TestUDMS/udmsapi";
  // const redirectURIApi = "https://aps3.zemenbank.com/UDMS/udmsapi";


  let apiEntitlementsIndexUrl = redirectURIApi + "/positions/manager/index";
  let apiEntitlementsApproveUrl = redirectURIApi + "/positions/manager/approve";
  let apiEntitlementsRejectUrl = redirectURIApi + "/positions/manager/reject";

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
      const queryParams = {
      };
      const rowData = await requestResourceWithCredsPost(apiEntitlementsIndexUrl, queryParams);
      if (rowData.status && rowData.status === 403) {
        setIsForbidden(true);
      }
      else {
        setRows(rowData);
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);


  const handleApproveButtonClick = async (row) => {
    try {
      const response = await requestResourceWithCredsPost(apiEntitlementsApproveUrl, row);
      //SUCCESS
      if (response) {
        setIsLoading(true);
        const queryParams = {
          positionCategoryName: selectedPositionCategory,
          page: page,
          rowsPerPage: rowsPerPage
        };
        const dataIndex = await requestResourceWithCredsPost(apiEntitlementsIndexUrl, queryParams);
        if (dataIndex) {
          setRows(dataIndex);
          setIsLoading(false);
        }
        setActionStatus(true);
        setOpenActionStatus(true);
      }
      else {
        setActionStatus(false);
        setOpenActionStatus(true);
      }
    } catch (error) {
      //ERROR
      setActionStatus(false);
      setOpenActionStatus(true);
    }
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleActionStatusClose = () => {
    setOpenActionStatus(false);
  };

  const handleActionClose = () => {
    setOpenAction(false);
  };

  const handleRejectButtonClick = (row) => {
    setSelectedEntitlement(row);
    setOpen(true);
  };

  const handleActionButtonClick = (row) => {
    setSelectedEntitlement(row);
    setOpenAction(true);
  };

  function FormDialog() {

    const [remark, setRemark] = useState("");
    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);


    const handleRejectActionButtonClick = async (event) => {
      if (remark) {
        let tempEntitlement = selectedEntitlement;
        tempEntitlement.remark = remark;
        setSelectedEntitlement(tempEntitlement);
        setErrorMessageHidden(false);
        try {
          const response = await requestResourceWithCredsPost(apiEntitlementsRejectUrl, selectedEntitlement);
          //SUCCESS
          if (response) {
            setIsLoading(true);
            const queryParams = {
              page: page,
              rowsPerPage: rowsPerPage
            };
            const dataIndex = await requestResourceWithCredsPost(apiEntitlementsIndexUrl, queryParams);
            if (dataIndex) {
              setRows(dataIndex);
              setIsLoading(false);
            }
            setActionStatus(true);
            setOpen(false);
            setOpenActionStatus(true);
          }
          else {
            setActionStatus(false);
            setOpen(false);
            setOpenActionStatus(true);
          }
        } catch (error) {
          //ERROR
          setActionStatus(false);
          setOpen(false);
          setOpenActionStatus(true);
        }
      }
      else {
        setErrorMessageHidden(true);
      }
    };

    const handleRemarkChange = (event) => {
      setRemark(event.target.value);
    };


    return (
      <Dialog
        open={open}
        onClose={handleClose}
      >
        <DialogTitle align='center'><span>Reject Entitlement</span>
        </DialogTitle>
        <DialogContent sx={{ minWidth: 600 }}>
          <Grid container spacing={2} >
            <Grid item xs={12}>
              {isErrorMessageHidden && <Alert severity="error" sx={{ m: 1, width: 1 }}>
                <AlertTitle>Error</AlertTitle>
                <Typography>Please fill the required fields marked(*)</Typography>
              </Alert>
              }
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <Paper elevation={5} sx={{ padding: 1 }}>
                  {selectedEntitlement?.positionCategory?.name}
                </Paper>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <Paper elevation={5} sx={{ padding: 1 }}>
                  {selectedEntitlement?.entitlementUniformItemsCollection?.at(0)?.itemName}
                </Paper>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  name="remark"
                  required
                  label="Remark"
                  multiline
                  rows={6}
                  value={remark}
                  onChange={handleRemarkChange}
                />
              </FormControl>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} variant="contained" color="secondary">Cancel</Button>
          <Button variant="contained" type="submit" onClick={handleRejectActionButtonClick}>Reject</Button>
        </DialogActions>
      </Dialog>
    );
  }

  const NoWrapPadTblCell = styled(TableCell)(({ theme }) => ({
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  }));


  function ActionDialog() {
    const [remark, setRemark] = useState("");
    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);

    const handleRejectActionButtonClick = async (event) => {
      let tempEntitlement = selectedEntitlement;
      tempEntitlement.remark = remark;
      setSelectedEntitlement(tempEntitlement);
      try {
        const response = await requestResourceWithCredsPost(apiEntitlementsRejectUrl, selectedEntitlement);
        //SUCCESS
        if (response) {
          setIsLoading(true);
          const queryParams = {
            page: page,
            rowsPerPage: rowsPerPage
          };
          const dataIndex = await requestResourceWithCredsPost(apiEntitlementsIndexUrl, queryParams);
          if (dataIndex) {
            setRows(dataIndex);
            setIsLoading(false);
          }
          setActionStatus(true);
          setOpenAction(false);
          setOpenActionStatus(true);
        }
        else {
          setActionStatus(false);
          setOpenAction(false);
          setOpenActionStatus(true);
        }
      } catch (error) {
        //ERROR
        setActionStatus(false);
        setOpenAction(false);
        setOpenActionStatus(true);
      }
    };

    const handleApproveActionButtonClick = async (event) => {
      // if (remark) {
      let tempEntitlement = selectedEntitlement;
      tempEntitlement.remark = remark;
      setSelectedEntitlement(tempEntitlement);
      setErrorMessageHidden(false);
      try {
        const response = await requestResourceWithCredsPost(apiEntitlementsApproveUrl, selectedEntitlement);
        //SUCCESS
        if (response) {
          setIsLoading(true);
          const queryParams = {
            page: page,
            rowsPerPage: rowsPerPage
          };
          const dataIndex = await requestResourceWithCredsPost(apiEntitlementsIndexUrl, queryParams);
          if (dataIndex) {
            setRows(dataIndex);
            setIsLoading(false);
          }
          setActionStatus(true);
          setOpenAction(false);
          setOpenActionStatus(true);
        }
        else {
          setActionStatus(false);
          setOpenAction(false);
          setOpenActionStatus(true);
        }
      } catch (error) {
        //ERROR
        setActionStatus(false);
        setOpenAction(false);
        setOpenActionStatus(true);
      }
    };
    
    const handleRemarkChange = (event) => {
      setRemark(event.target.value);
    };


    return (
      <Dialog open={openAction}
        onClose={handleActionClose}
      >
        <DialogTitle align='center'>
          <Grid item xs={12}><span>Approve Entitlement</span></Grid>
          <Grid item xs={12}>
            {isErrorMessageHidden && <Alert severity="error" sx={{ m: 1, width: 1 }}>
              <AlertTitle>Error</AlertTitle>
              <Typography>Please fill the required fields marked(*)</Typography>
            </Alert>
            }
          </Grid>
        </DialogTitle>
        <DialogContent sx={{ minWidth: 600 }}>
          <Grid container spacing={2} >
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <Paper elevation={5} sx={{ padding: 1 }}>
                  {selectedEntitlement?.positionCategory?.name}
                </Paper>
              </FormControl>
            </Grid>
            {/* ################################################################################################### */}
            <TableContainer>
              <Table>
                <TableHead>
                  {(
                    <TableRow>
                      <NoWrapPadTblCell>Item</NoWrapPadTblCell>
                      <NoWrapPadTblCell>Quantity</NoWrapPadTblCell>
                      <NoWrapPadTblCell>Period</NoWrapPadTblCell>
                      <NoWrapPadTblCell>Prepared</NoWrapPadTblCell>
                      <NoWrapPadTblCell>Date</NoWrapPadTblCell>
                    </TableRow>
                  )}
                </TableHead>
                <TableBody>
                  {selectedEntitlement && selectedEntitlement.entitlementUniformItemsCollection && selectedEntitlement.entitlementUniformItemsCollection.map(row => (
                    <TableRow key={row.imsItemId}>
                      <NoWrapPadTblCell>{row.itemName}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{row.quantity}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{row.period + " Years"}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{selectedEntitlement.createdBy}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{dayjs(selectedEntitlement.createdDate).format("MMM/DD/YYYY")}</NoWrapPadTblCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  name="remark"
                  required
                  label="Remark"
                  multiline
                  rows={3}
                  value={remark}
                  onChange={handleRemarkChange}
                />
              </FormControl>
            </Grid>
            {/* #################################################################################################### */}
          </Grid>
        </DialogContent>
        <DialogActions>
        <Button variant="contained" color="secondary" type="submit" onClick={handleRejectActionButtonClick}>Reject</Button>
        <Button variant="contained" type="submit" onClick={handleApproveActionButtonClick}>Approve</Button>
        </DialogActions>
      </Dialog>
    );
  }

  if (isForbidden) {
    return (
      <div>
        <Alert variant="outlined" severity="error">
          Access is forbidden.
        </Alert>
      </div>
    )
  }

  if (isLoading) {
    return <div><LinearProgress /></div>;
  }

  return (
    <>
      <div>
        <FormDialog />
        <div>
          <Dialog open={openActionStatus} onClose={handleActionStatusClose}>
            <DialogTitle align='center'>Action Status</DialogTitle>
            <DialogContent>
              {actionStatus && <Alert severity="success">Action Success.</Alert>}
              {!actionStatus && <Alert severity="error">Action Failure.</Alert>}
            </DialogContent>
          </Dialog>
        </div>
        <ActionDialog />
      </div>

      <Grid>
        <TableContainer>
          <Table>
            <TableHead>
              {(
                <TableRow>
                  <NoWrapPadTblCell>Name</NoWrapPadTblCell>
                  <TableCell>Prepared</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell>Action</TableCell>
                </TableRow>
              )}
            </TableHead>
            <TableBody>
              {rows.map((row) => (
                <TableRow>
                  <NoWrapPadTblCell>{row.positionCategory?.name}</NoWrapPadTblCell>
                  <TableCell>{row.createdBy}</TableCell>
                  <TableCell>{dayjs(row.createdDate).format("MMM/DD/YYYY")}</TableCell>
                  <TableCell>
                    <Grid container>
                      <Grid item xs={3}>
                        <Button variant="contained" type='button' size='small' onClick={() => handleActionButtonClick(row)}>Action</Button>
                      </Grid>
                    </Grid>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Grid>
    </>
  );
}
