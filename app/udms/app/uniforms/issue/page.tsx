'use client'
import { Alert, AlertTitle, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, Grid, InputLabel, LinearProgress, MenuItem, OutlinedInput, Paper, Select, SelectChangeEvent, TableHead, TextField, Typography } from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import { styled } from '@mui/material/styles';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { refreshInactiveToken, requestResourceWithCredsPost } from "../../services/ServiceUtil";

export default function Page() {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(4);
  const [rows, setRows] = useState<UniformRequisition[]>([]);
  const [selectedDepartmentName, setSelectedDepartmentName] = useState("");
  const [departmentNames, setDepartmentNames] = useState<IdNamePair[]>([]);
  const [openAction, setOpenAction] = useState(false);
  const [openActionStatus, setOpenActionStatus] = useState(false);
  const [actionStatus, setActionStatus] = useState('');
  const [selectedUniformRequisition, setSelectedUniformRequisition] = useState<UniformRequisition>(null);
  const [uniformRequisitionDetailRows, setUniformRequisitionDetailRows] = useState<UniformRequisitionDetail[]>([]);

  const [isLoading, setIsLoading] = useState(true);
  const [isForbidden, setIsForbidden] = useState(false);

  // const redirectURIApi = "http://localhost:8082/udmsapi";
  const redirectURIApi = "https://aps3.zemenbank.com/TestUDMS/udmsapi";
  // const redirectURIApi = "https://aps3.zemenbank.com/UDMS/udmsapi";


  let apiUniformIndexUrl = redirectURIApi + "/uniforms/officer/index";
  let apiIssueOfficerCreateUrl = redirectURIApi + "/uniforms/officer/createissue";
  let apiIssueOfficerRejectUrl = redirectURIApi + "/uniforms/officer/rejectissue";
  let apiEmployeesCommonDepartmentNames = redirectURIApi + "/employees/common/departmentNames";
  let apiUniformsCommonEmployeeUniformIssueItems = redirectURIApi + "/uniforms/common/employeeuniformissueitems";

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
      console.log("fetchData : selectedDepartmentName === " + selectedDepartmentName);
      const queryParams = {
        department: selectedDepartmentName
      };
      const rowData = await requestResourceWithCredsPost(apiUniformIndexUrl, queryParams);
      if (rowData.status && rowData.status === 403) {
        setIsForbidden(true);
      }
      else {
        setRows(rowData);
        const dataDepNames = await requestResourceWithCredsPost(apiEmployeesCommonDepartmentNames, []);
        setDepartmentNames(dataDepNames);
        setIsLoading(false);
      }
    };
    fetchData();
  }, [selectedDepartmentName]);

  const handleActionStatusClose = () => {
    setOpenActionStatus(false);
  };

  const handleActionClose = () => {
    setOpenAction(false);
  };

  const handleActionButtonClick = (row) => {
    requestResourceWithCredsPost(apiUniformsCommonEmployeeUniformIssueItems, row).then(data => {
      setSelectedUniformRequisition(row);
      setUniformRequisitionDetailRows(data);
      setOpenAction(true);
    });
  };

  const NoWrapPadTblCell = styled(TableCell)(({ theme }) => ({
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  }));


  function ActionDialog() {
    const [remark, setRemark] = useState("");
    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);

    const handleRejectActionButtonClick = async (event) => {
      let uniformRequisition: UniformRequisition = {
        id: selectedUniformRequisition.id,
        department: '',
        createdBy: '',
        createdDate: undefined,
        approvedBy: '',
        approvedDate: undefined,
        uniformRequisitionDetailCollection: [],
        employeeId: undefined,
        luStatusId: undefined,
        remark: remark
      }

      const response = await requestResourceWithCredsPost(apiIssueOfficerRejectUrl, uniformRequisition);
      //SUCCESS
      setOpenAction(false);
      setActionStatus('form-success');
      setOpenActionStatus(true);
      setIsLoading(true);
      try {
        if (response) {
          const queryParams = {
            department: selectedDepartmentName
          };
          const dataIndex = await requestResourceWithCredsPost(apiUniformIndexUrl, queryParams);
          if (dataIndex) {
            setRows(dataIndex);
            const dataDepNames = await requestResourceWithCredsPost(apiEmployeesCommonDepartmentNames, []);
            setDepartmentNames(dataDepNames);
            setIsLoading(false);
          }
        }
        else {
          setOpenAction(false);
          setActionStatus('form-fail');
          setOpenActionStatus(true);
        }
      }
      catch (error) {
        //ERROR
        setOpenAction(false);
        setActionStatus('form-fail');
        setOpenActionStatus(true);
      }
    };

    const handleSubmitIssueForApproval = async (event) => {
      let uniformRequisition: UniformRequisition = {
        id: selectedUniformRequisition.id,
        department: '',
        createdBy: '',
        createdDate: undefined,
        approvedBy: '',
        approvedDate: undefined,
        uniformRequisitionDetailCollection: [],
        employeeId: undefined,
        luStatusId: undefined,
        remark: remark
      }

      const response = await requestResourceWithCredsPost(apiIssueOfficerCreateUrl, uniformRequisition);
      //SUCCESS
      setOpenAction(false);
      setActionStatus('form-success');
      setOpenActionStatus(true);
      setIsLoading(true);
      try {
        if (response) {
          const queryParams = {
            department: selectedDepartmentName
          };
          const dataIndex = await requestResourceWithCredsPost(apiUniformIndexUrl, queryParams);
          if (dataIndex) {
            setRows(dataIndex);
            const dataDepNames = await requestResourceWithCredsPost(apiEmployeesCommonDepartmentNames, []);
            setDepartmentNames(dataDepNames);
            setIsLoading(false);
          }
        }
        else {
          setOpenAction(false);
          setActionStatus('form-fail');
          setOpenActionStatus(true);
        }
      }
      catch (error) {
        //ERROR
        setOpenAction(false);
        setActionStatus('form-fail');
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
          <Grid item xs={12}><span>Issue</span></Grid>
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
                  {selectedUniformRequisition?.employeeId?.name}
                </Paper>
              </FormControl>
            </Grid>
            <TableContainer>
              <TableHead>
                {(
                  <TableRow>
                    <NoWrapPadTblCell>Last Date</NoWrapPadTblCell>
                    <NoWrapPadTblCell>Item</NoWrapPadTblCell>
                    <NoWrapPadTblCell>Size</NoWrapPadTblCell>
                    <NoWrapPadTblCell>Prepared</NoWrapPadTblCell>
                    <NoWrapPadTblCell>Next Date</NoWrapPadTblCell>
                  </TableRow>
                )}
              </TableHead>
              <TableBody>
                {uniformRequisitionDetailRows.map(row => (
                    <TableRow key={row.imsItemId}>
                      <NoWrapPadTblCell>{row.lastIssueDate ? dayjs(row.lastIssueDate).format("MMM/DD/YYYY") : ''}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{row.itemName}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{row.size}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{row.approvedBy}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{row.nextIssueDate ? dayjs(row.nextIssueDate).format("MMM/DD/YYYY") : ''}</NoWrapPadTblCell>
                    </TableRow>
                  ))}
              </TableBody>
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
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button variant="contained" color="secondary" type="submit" onClick={handleRejectActionButtonClick}>Reject</Button>
          <Button variant="contained" type="submit" onClick={handleSubmitIssueForApproval}>Submit For Approval</Button>
        </DialogActions>
      </Dialog>
    );
  }

  function DepartmentNameSelectInput() {

    const ITEM_HEIGHT = 48;
    const ITEM_PADDING_TOP = 8;
    const MenuProps = {
      PaperProps: {
        style: {
          maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
          width: 250,
        },
      },
    };

    const handleChange = (event: SelectChangeEvent) => {
      setSelectedDepartmentName(event.target.value);
    };

    return (
      <div>
        <FormControl sx={{ m: 0, width: 1 }} >
          <InputLabel id="department-label">Department/Branch</InputLabel>
          <Select
            labelId="department-label"
            value={selectedDepartmentName}
            onChange={handleChange}
            input={<OutlinedInput label="Department/Branch" />}
            MenuProps={MenuProps}
          >
            {departmentNames && departmentNames.map((departmentName) => (
              <MenuItem
                key={departmentName.id}
                value={departmentName.name}
              >
                {departmentName.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </div>
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

        <Grid container spacing={2} paddingTop={2}>
          <Grid item xs={4}>
            <DepartmentNameSelectInput />
          </Grid>
        </Grid>

        <TableContainer>
          <Table>
            <TableHead>
              {(
                <TableRow>
                  <NoWrapPadTblCell>Badge Number</NoWrapPadTblCell>
                  <NoWrapPadTblCell>Name</NoWrapPadTblCell>
                  <NoWrapPadTblCell>Category</NoWrapPadTblCell>
                  <NoWrapPadTblCell>Gender</NoWrapPadTblCell>
                  <NoWrapPadTblCell>Branch</NoWrapPadTblCell>
                  <TableCell>Approved</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell>Action</TableCell>
                </TableRow>
              )}
            </TableHead>
            <TableBody>
              {rows.map((row) => (
                <TableRow>
                  <NoWrapPadTblCell>{row.employeeId?.badgeNumber}</NoWrapPadTblCell>
                  <NoWrapPadTblCell>{row.employeeId?.name}</NoWrapPadTblCell>
                  <NoWrapPadTblCell>{row.employeeId?.positionCategory?.name}</NoWrapPadTblCell>
                  <NoWrapPadTblCell>{row.employeeId?.gender}</NoWrapPadTblCell>
                  <NoWrapPadTblCell>{row.department}</NoWrapPadTblCell>
                  <TableCell>{row.approvedBy}</TableCell>
                  <TableCell>{dayjs(row.approvedDate).format("MMM/DD/YYYY")}</TableCell>
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
