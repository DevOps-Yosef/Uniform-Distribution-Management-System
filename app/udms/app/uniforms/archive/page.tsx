'use client'
import { Alert, AlertTitle, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, Grid, InputLabel, LinearProgress, MenuItem, OutlinedInput, Paper, Select, SelectChangeEvent, TableHead, TablePagination, TextField, Typography } from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import { styled } from '@mui/material/styles';
import { useEffect, useState } from 'react';
import { refreshInactiveToken, requestReports, requestResourceWithCredsPost } from "../../services/ServiceUtil";
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs, { Dayjs } from 'dayjs';

export default function Page() {
  const [page, setPage] = useState(0);
  const [totalCount, setTotalCount] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(4);
  const [rows, setRows] = useState<UniformRequisition[]>([]);
  const [openAction, setOpenAction] = useState(false);
  const [openActionStatus, setOpenActionStatus] = useState(false);
  const [actionStatus, setActionStatus] = useState('');
  const [selectedUniformRequisition, setSelectedUniformRequisition] = useState<UniformRequisition>(null);
  const [uniformRequisitionDetailRows, setUniformRequisitionDetailRows] = useState<UniformRequisitionDetail[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isForbidden, setIsForbidden] = useState(false);
  const [loadingReport, setLoadingReport] = useState(false);
  const [loadingReportSignature, setLoadingReportSignature] = useState(false);
  const [uniformNeedsCollectionReport, setUniformNeedsCollectionReport] = useState(null);
  const [dateFrom, setDateFrom] = useState<Dayjs | null>(null);
  const [dateTo, setDateTo] = useState<Dayjs | null>(null);
  const [departmentNames, setDepartmentNames] = useState<IdNamePair[]>([]);
  const [selectedDepartmentName, setSelectedDepartmentName] = useState("");
  const [isBranchUser, setIsBranchUser] = useState(false);
  const [positionCategories, setPositionCategories] = useState<PositionCategory[]>([]);
  const [selectedPositionCategory, setSelectedPositionCategory] = useState('');

  // const redirectURIApi = "http://localhost:8082/udmsapi";
  const redirectURIApi = "https://aps3.zemenbank.com/TestUDMS/udmsapi";
  // const redirectURIApi = "https://aps3.zemenbank.com/UDMS/udmsapi";


  let apiUniformIndexUrl = redirectURIApi + "/uniforms/common/requestarchive";
  let apiUniformsCommonRequestArchiveCountUrl = redirectURIApi + "/uniforms/common/requestarchive/count";
  let apiUniformsCommonEmployeeUniformIssueItems = redirectURIApi + "/uniforms/common/employeeuniformissueitems";
  let apiUniformsCommonRequestArchiveDownloadSignature = redirectURIApi + "/uniforms/common/requestarchivedownloadsignature";
  let apiUniformsCommonDepartmentNames = redirectURIApi + "/uniforms/common/departmentNames";
  let apiUniformsCommonSelectedDepartment = redirectURIApi + "/uniforms/common/selectedDepartment";
  let apiUniformsCommonIsBranchUser = redirectURIApi + "/uniforms/common/isbranchuser";
  let apiRequestCommonPositionCategories = redirectURIApi + "/uniforms/common/positionCategories";

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
        dateFrom: dateFrom,
        dateTo: dateTo,
        department: selectedDepartmentName,
        positionCategoryName: selectedPositionCategory,
        page: page,
        rowsPerPage: rowsPerPage
      };
      const dataIndex = await requestResourceWithCredsPost(apiUniformIndexUrl, queryParams);
      if (dataIndex.status && dataIndex.status === 403) {
        setIsForbidden(true);
      }
      else {
        setRows(dataIndex);
        const dataCount = await requestResourceWithCredsPost(apiUniformsCommonRequestArchiveCountUrl, queryParams);
        setTotalCount(dataCount);
        const dataDepNames = await requestResourceWithCredsPost(apiUniformsCommonDepartmentNames, queryParams);
        setDepartmentNames(dataDepNames);
        const dataPosCategory = await requestResourceWithCredsPost(apiRequestCommonPositionCategories, queryParams);
        setPositionCategories(dataPosCategory);
        setIsLoading(false);
        setIsBranchUser(await requestResourceWithCredsPost(apiUniformsCommonIsBranchUser, []));
        if(isBranchUser)
        {
          const tempNameObject: NameObject = await requestResourceWithCredsPost(apiUniformsCommonSelectedDepartment, []);
          setSelectedDepartmentName(tempNameObject.name);
        }
      }
    };
    fetchData();
  }, [page, rowsPerPage, selectedDepartmentName, dateFrom, dateTo, selectedPositionCategory]);

  useEffect(() => {
    if (uniformNeedsCollectionReport) {
      try {
        if (typeof window !== 'undefined') {
          const url = window.URL.createObjectURL(uniformNeedsCollectionReport);
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', 'Uniform Needs Collection Report.xlsx');
          document.body.appendChild(link);
          link.click();
          window.URL.revokeObjectURL(url);
          link.remove();
          setLoadingReport(false);
          setLoadingReportSignature(false);
        } 
      } catch (error) {
      }
    }
  }, [uniformNeedsCollectionReport]);

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

  
  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const DepartmentNameSelectInput = ({ isDisabled }) => {
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
      setPage(0);
    };

    return (
      <div>
        <FormControl sx={{ m: 0, width: 1 }} >
          <InputLabel id="department-label">Department/Branch</InputLabel>
          <Select disabled={isDisabled} 
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

  function PositionCategorySelectInput() {

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
      setSelectedPositionCategory(event.target.value);
      setPage(0);
    };

    return (
      <div>
        <FormControl sx={{ m: 0, width: 1 }} >
          <InputLabel id="category-label">Category</InputLabel>
          <Select
            labelId="category-label"
            value={selectedPositionCategory}
            onChange={handleChange}
            input={<OutlinedInput label="Category" />}
            MenuProps={MenuProps}
          >
            {positionCategories && positionCategories.map((positionCategory) => (
              <MenuItem
                key={positionCategory.id}
                value={positionCategory.name}
              >
                {positionCategory.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </div>
    );
  }

  const handleDownloadSignature = () => {
    try {
      if (!dateFrom?.isValid || !dateTo?.isValid) {
        setActionStatus('form-fail');
        setOpenActionStatus(true);
        return;
      }
      setLoadingReportSignature(true);
      const queryParams = {
        dateFrom: dateFrom,
        dateTo: dateTo,
        positionCategoryName: selectedPositionCategory,
        department: selectedDepartmentName
      };
      requestReports(apiUniformsCommonRequestArchiveDownloadSignature, queryParams)
        .then(data => {
          const binaryData = Buffer.from(data.byteArr);
          const blob = new Blob([binaryData.buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          setUniformNeedsCollectionReport(blob);
        }
        )
        .catch(error => {
          console.log(error);
        });
    } catch (error) {
    }
  };

  function ActionDialog() {
    const [remark, setRemark] = useState("");
    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);

    const handleRemarkChange = (event) => {
      setRemark(event.target.value);
    };


    return (
      <Dialog open={openAction}
        onClose={handleActionClose}
      >
        <DialogTitle align='center'>
          <Grid item xs={12}><span>View Request</span></Grid>
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
              <Table>
                <TableHead>
                  {(
                    <TableRow>
                      <NoWrapPadTblCell>Last Date</NoWrapPadTblCell>
                      <NoWrapPadTblCell>Item</NoWrapPadTblCell>
                      <NoWrapPadTblCell>Size</NoWrapPadTblCell>
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
                      <NoWrapPadTblCell>{row.nextIssueDate ? dayjs(row.nextIssueDate).format("MMM/DD/YYYY") : ''}</NoWrapPadTblCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
            <Grid item xs={2}>
              Remark
            </Grid>
            <Grid item xs={10}>
              {uniformRequisitionDetailRows?.at(0)?.uniformRequisitionId?.remark}
            </Grid>
          </Grid>
        </DialogContent>
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
        <div>
          <Dialog open={openActionStatus} onClose={handleActionStatusClose}>
            <DialogTitle align='center'>Action Status</DialogTitle>
            <DialogContent>
              {actionStatus && actionStatus === 'form-fail' && <Alert severity="error">Select Date.</Alert>}
            </DialogContent>
          </Dialog>
        </div>
        <ActionDialog />
      </div>

      <Grid>
        <Grid container spacing={2} paddingTop={2}>
          <Grid item xs={2}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker name="dateFrom"
                label="Date From" value={dateFrom}
                onChange={(newValue) => setDateFrom(newValue)}
              />
            </LocalizationProvider>
          </Grid>
          <Grid item xs={2}>
            <LocalizationProvider dateAdapter={AdapterDayjs}>
              <DatePicker name="dateTo"
                label="Date To" value={dateTo}
                onChange={(newValue) => setDateTo(newValue)}
              />
            </LocalizationProvider>
          </Grid>
          <Grid item xs={3}>
            <DepartmentNameSelectInput isDisabled={isBranchUser} />
          </Grid>
          <Grid item xs={3}>
            <PositionCategorySelectInput />
          </Grid>
          <Grid item xs={2}>
            <Button variant="contained" onClick={handleDownloadSignature} disabled={loadingReportSignature}>
              {loadingReportSignature ? 'Downloading...' : 'Signature'}
            </Button>
          </Grid>
        </Grid>
        <TableContainer>
          <Table>
            <TableHead>
              {(
                <TableRow>
                  <NoWrapPadTblCell>Department</NoWrapPadTblCell>
                  <NoWrapPadTblCell>Name</NoWrapPadTblCell>
                  <NoWrapPadTblCell>Category</NoWrapPadTblCell>
                  <TableCell>Prepared</TableCell>
                  <TableCell>Date</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Action</TableCell>
                </TableRow>
              )}
            </TableHead>
            <TableBody>
              {rows.map((row) => (
                <TableRow>
                  <NoWrapPadTblCell>{row.department}</NoWrapPadTblCell>
                  <NoWrapPadTblCell>{row.employeeId?.name}</NoWrapPadTblCell>
                  <NoWrapPadTblCell>{row.employeeId?.positionCategory?.name}</NoWrapPadTblCell>
                  <TableCell>{row.createdBy}</TableCell>
                  <TableCell>{dayjs(row.approvedDate).format("MMM/DD/YYYY")}</TableCell>
                  <TableCell>{row?.luStatusId?.name}</TableCell>
                  <TableCell>
                    <Grid container>
                      <Grid item xs={3}>
                        <Button variant="contained" type='button' size='small' onClick={() => handleActionButtonClick(row)}>View</Button>
                      </Grid>
                    </Grid>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
        rowsPerPageOptions={[4, 10, 25]}
        component="div"
        count={totalCount}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage} />
      </Grid>
    </>
  );
}
