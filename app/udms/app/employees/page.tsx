'use client'
import { Add } from '@mui/icons-material';
import { Alert, AlertTitle, Button, Dialog, DialogActions, DialogContent, DialogTitle, FormControl, Grid, InputLabel, LinearProgress, MenuItem, OutlinedInput, Select, SelectChangeEvent, TableCell, TableHead, TextField, Typography } from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import { styled } from '@mui/material/styles';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { refreshInactiveToken, requestResourceWithCredsPost, requestReports } from "../services/ServiceUtil";

export default function Page() {
  const [page, setPage] = useState(0);
  const [totalCount, setTotalCount] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(4);
  const [rows, setRows] = useState<Employee[]>([]);
  const [name, setName] = useState("");
  const [departmentNames, setDepartmentNames] = useState<IdNamePair[]>([]);
  const [selectedDepartmentName, setSelectedDepartmentName] = useState("");
  const [positionCategories, setPositionCategories] = useState<PositionCategory[]>([]);
  const [selectedPositionCategory, setSelectedPositionCategory] = useState('');
  const [selectedGender, setSelectedGender] = useState('');
  const [selectedStatus, setSelectedStatus] = useState('');
  const [open, setOpen] = useState(false);
  const [employeeUniformItemRows, setEmployeeUniformItemRows] = useState<EmployeeUniformItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedEmployee, setSelectedEmployee] = useState<Employee>();
  const [openEdit, setOpenEdit] = useState(false);
  const [openAdd, setOpenAdd] = useState(false);
  const [actionStatus, setActionStatus] = useState(false);
  const [openActionStatus, setOpenActionStatus] = useState(false);
  const [loadingReport, setLoadingReport] = useState(false);
  const [entitledStaffReport, setEntitledStaffReport] = useState(null);

  // const redirectURIApi = "http://localhost:8082/udmsapi";
const redirectURIApi = "https://aps3.zemenbank.com/TestUDMS/udmsapi";
  // const redirectURIApi = "https://aps3.zemenbank.com/UDMS/udmsapi";

  let apiEmployeesIndexUrl = redirectURIApi + "/employees/common/index";
  let apiEmployeesCommonPositionCategories = redirectURIApi + "/employees/common/positionCategories";
  let apiEmployeesCommonDepartmentNames = redirectURIApi + "/employees/common/departmentNames";
  let apiEmployeesCommonEmployeeUniformItems = redirectURIApi + "/employees/common/employeeuniformitems";
  let apiEmployeesCountUrl = redirectURIApi + "/employees/common/count";
  let apiEmployeesOfficerCreateUrl = redirectURIApi + "/employees/officer/create";
  let apiEmployeesOfficerEditUrl = redirectURIApi + "/employees/officer/edit";
  let apiEmployeesDownloadEntitledStaffReportUrl = redirectURIApi + "/employees/common/download";

  const genders = [
    { id: 'F', name: 'F' },
    { id: 'M', name: 'M' },
  ];

  const statuses = [
    { id: '0', name: 'Active' },
    { id: '1', name: 'Inactive' },
  ];

  useEffect(() => {
    try {
      refreshInactiveToken().then(data => {
    });
    } catch (error) {
    }
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      const queryParams = {
        name: name,
        gender: selectedGender,
        status: selectedStatus,
        positionCategoryName: selectedPositionCategory,
        department: selectedDepartmentName,
        page: page,
        rowsPerPage: rowsPerPage
      };
      await refreshInactiveToken();
      const dataIndex = await requestResourceWithCredsPost(apiEmployeesIndexUrl, queryParams);
      setRows(dataIndex);
      const dataCount = await requestResourceWithCredsPost(apiEmployeesCountUrl, queryParams);
      setTotalCount(dataCount);
      const dataPosCategory = await requestResourceWithCredsPost(apiEmployeesCommonPositionCategories, queryParams);
      setPositionCategories(dataPosCategory);
      const dataDepNames = await requestResourceWithCredsPost(apiEmployeesCommonDepartmentNames, queryParams);
      setDepartmentNames(dataDepNames);
      setIsLoading(false);
    };
    fetchData();
  }, [page, rowsPerPage, name, selectedGender, selectedPositionCategory, selectedDepartmentName, selectedStatus]);

  useEffect(() => {
    if (entitledStaffReport) {
      try {
        if (typeof window !== 'undefined') {
          const url = window.URL.createObjectURL(entitledStaffReport);
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', 'Entitled Staff Report.xlsx');
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
  }, [entitledStaffReport]);

  const NoWrapPadTblCell = styled(TableCell)(({ theme }) => ({
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  }));

  const PadTblCell = styled(TableCell)(({ theme }) => ({
    // padding: theme.spacing(0.5)
  }));


  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  function GenderSelectInput() {

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
      setSelectedGender(event.target.value);
    };

    return (
      <div>
        <FormControl sx={{ m: 0, width: 1 }} >
          <InputLabel id="gender-label">Gender</InputLabel>
          <Select
            labelId="gender-label"
            value={selectedGender}
            onChange={handleChange}
            input={<OutlinedInput label="Gender" />}
            MenuProps={MenuProps}
          >
            {genders && genders.map((gender) => (
              <MenuItem
                key={gender.id}
                value={gender.name}
              >
                {gender.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </div>
    );
  }


  function StatusSelectInput() {

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
      setSelectedStatus(event.target.value);
    };

    return (
      <div>
        <FormControl sx={{ m: 0, width: 1 }} >
          <InputLabel id="status-label">Status</InputLabel>
          <Select
            labelId="status-label"
            value={selectedStatus}
            onChange={handleChange}
            input={<OutlinedInput label="Status" />}
            MenuProps={MenuProps}
          >
            {statuses && statuses.map((status) => (
              <MenuItem
                key={status.id}
                value={status.id}
              >
                {status.name}
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


  const handleUniformButtonClick = (row) => {
    requestResourceWithCredsPost(apiEmployeesCommonEmployeeUniformItems, row).then(data => {
      setEmployeeUniformItemRows(data);
      setOpen(true);
    });
  };

  const handleEditButtonClick = (row) => {
    setSelectedEmployee(row);
    setOpenEdit(true);
  };

  const handleAddButtonClick = () => {
    setOpenAdd(true);
  };


  const handleClose = () => {
    setOpen(false);
  };


  const handleEditClose = () => {
    setOpenEdit(false);
  };

  const handleAddClose = () => {
    setOpenAdd(false);
  };

  const handleActionStatusClose = () => {
    setOpenActionStatus(false);
  };

  function FormDialog() {
    return (
      <Dialog
        open={open}
        onClose={handleClose}
      >
        <DialogTitle align='center'>
          <span>Entitled Staff Uniform Items</span>
        </DialogTitle>
        <DialogContent sx={{ minWidth: 600 }}>
          <Grid container spacing={2} >
            <TableContainer>
              <Table>
                <TableHead>
                  {(
                    <TableRow>
                      <NoWrapPadTblCell>Date</NoWrapPadTblCell>
                      <NoWrapPadTblCell>Item</NoWrapPadTblCell>
                      <NoWrapPadTblCell>Size</NoWrapPadTblCell>
                      <NoWrapPadTblCell>Next Date</NoWrapPadTblCell>
                    </TableRow>
                  )}
                </TableHead>
                <TableBody>
                  {employeeUniformItemRows && (employeeUniformItemRows.map(row => (
                    <TableRow key={row.idUniformIssue}>
                      <NoWrapPadTblCell>{row.date ? dayjs(row.date).format("MMM/DD/YYYY") : ''}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{row.itemName}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{row.size}</NoWrapPadTblCell>
                      <NoWrapPadTblCell>{row.nextDate ? dayjs(row.nextDate).format("MMM/DD/YYYY") : ''}</NoWrapPadTblCell>
                    </TableRow>
                  )))}
                </TableBody>
              </Table>
            </TableContainer>
          </Grid>
        </DialogContent>
      </Dialog>
    );
  }

  function EmployeeToEditDialog() {
    const [badgeNumber, setBadgeNumber] = useState("");
    const [name, setName] = useState("");
    const [gender, setGender] = useState("");
    const [positionCategoryName, setPositionCategoryName] = useState("");
    const [jobPosition, setJobPosition] = useState("");
    const [jobGrade, setJobGrade] = useState("");
    const [jobCategory, setJobCategory] = useState("");
    const [department, setDepartment] = useState("");
    const [status, setStatus] = useState("");
    const [eMail, setEMail] = useState("");


    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);

    useEffect(() => {
      if (openEdit) {
        setBadgeNumber(selectedEmployee.badgeNumber);
        setName(selectedEmployee.name);
        setGender(selectedEmployee.gender);
        setPositionCategoryName(selectedEmployee.positionCategoryName);
        setJobPosition(selectedEmployee.jobPosition);
        setJobGrade(selectedEmployee.jobGrade);
        setJobCategory(selectedEmployee.jobCategory);
        setDepartment(selectedEmployee.department);
        setStatus(selectedEmployee.status);
        setEMail(selectedEmployee.eMail);
      }
    }, [openEdit]);


    function GenderSelectInputEdit() {
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

      return (
        <div>
          <FormControl sx={{ m: 1, width: 1 }} >
            <InputLabel id="gender-label">Gender *</InputLabel>
            <Select
              required
              labelId="gender-label"
              label="Gender *"
              value={gender}
              onChange={handleGender}
              // input={<OutlinedInput />}
              MenuProps={MenuProps}
            >
              {genders && genders.map((gender) => (
                <MenuItem
                  key={gender.id}
                  value={gender.name}
                >
                  {gender.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </div>
      );
    }

    function PositionCategorySelectInputEdit() {

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

      return (
        <div>
          <FormControl sx={{ m: 1, width: 1 }} >
            <InputLabel id="category-label">Category *</InputLabel>
            <Select
              required
              labelId="category-label"
              value={positionCategoryName}
              onChange={handlePositionCategory}
              label="Category *"
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

    function DepartmentNameSelectInputEdit() {

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

      return (
        <div>
          <FormControl sx={{ m: 1, width: 1 }} >
            <InputLabel id="department-label">Department/Branch *</InputLabel>
            <Select
              required
              labelId="department-label"
              value={department}
              onChange={handleDepartment}
              label="Department/Branch *"
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

    function StatusSelectInputEdit() {

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

      return (
        <div>
          <FormControl sx={{ m: 1, width: 1 }} >
            <InputLabel id="status-label">Status *</InputLabel>
            <Select
              required
              labelId="status-label"
              value={status}
              onChange={handleStatus}
              label="Status *"
              MenuProps={MenuProps}
            >
              {statuses && statuses.map((status) => (
                <MenuItem
                  key={status.id}
                  value={status.id}
                >
                  {status.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </div>
      );
    }

    const handleEditEmployee = (event) => {
      if (badgeNumber && name && positionCategoryName && gender && jobPosition && department && status) {
        const employeeToEdit: Employee = {
          id: selectedEmployee.id,
          badgeNumber: badgeNumber,
          name: name,
          positionCategoryName: positionCategoryName,
          gender: gender,
          jobPosition: jobPosition,
          jobGrade: jobGrade,
          department: department,
          jobCategory: jobCategory,
          eMail: eMail,
          status: status,
          inactive: false,
          createdDate: undefined,
          createdBy: '',
          uniformRequisitionCollection: [],
          positionCategory: undefined,
          page: '',
          rowsPerPage: ''
        };
        setErrorMessageHidden(false);
        requestResourceWithCredsPost(apiEmployeesOfficerEditUrl, employeeToEdit).then(response => {
          //SUCCESS
          if (response) {
            setOpenEdit(false);
            setActionStatus(true);
            setOpenActionStatus(true);
            setIsLoading(true);
            const queryParams = {
              page: page,
              rowsPerPage: rowsPerPage
            };
            requestResourceWithCredsPost(apiEmployeesIndexUrl, queryParams).then(data => {
              setRows(data);
              requestResourceWithCredsPost(apiEmployeesCountUrl, queryParams).then(data => {
                setTotalCount(data);
                setIsLoading(false);
              });
            });
          }
          else {
            setOpenEdit(false);
            setActionStatus(false);
            setOpenActionStatus(true);
          }
        })
          .catch(error => {
            //ERROR
            setOpenEdit(false);
            setActionStatus(false);
            setOpenActionStatus(true);
          });
      }
      else {
        setErrorMessageHidden(true);
      }
    };

    const handleBadgeNumber = (event) => {
      setBadgeNumber(event.target.value);
    };

    const handleName = (event) => {
      setName(event.target.value);
    };

    const handleGender = (event) => {
      setGender(event.target.value);
    };

    const handlePositionCategory = (event) => {
      setPositionCategoryName(event.target.value);
    };

    const handleJobPosition = (event) => {
      setJobPosition(event.target.value);
    };

    const handleJobGrade = (event) => {
      setJobGrade(event.target.value);
    };

    const handleJobCategory = (event) => {
      setJobCategory(event.target.value);
    };

    const handleDepartment = (event) => {
      setDepartment(event.target.value);
    };

    const handleStatus = (event) => {
      setStatus(event.target.value);
    };

    const handleEMail = (event) => {
      setEMail(event.target.value);
    };



    return (
      <Dialog
        open={openEdit}
        onClose={handleEditClose}
      >
        <DialogTitle align='center'>
          <Grid item xs={12}><span>Edit Employee</span></Grid>
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
              {isErrorMessageHidden && <Alert severity="error" sx={{ m: 1, width: 1 }}>
                <AlertTitle>Error</AlertTitle>
                <Typography>Please fill the required fields marked(*)</Typography>
              </Alert>
              }
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="badgeNumber"
                  label="Badge Number"
                  value={badgeNumber}
                  onChange={handleBadgeNumber}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="name"
                  label="Name"
                  value={name}
                  onChange={handleName}
                />
              </FormControl>
            </Grid>
            <Grid item xs={8}>
              <PositionCategorySelectInputEdit />
            </Grid>
            <Grid item xs={4}>
              <GenderSelectInputEdit />
            </Grid>
            <Grid item xs={8}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="jobPosition"
                  label="Job Position"
                  value={jobPosition}
                  onChange={handleJobPosition}
                />
              </FormControl>
            </Grid>
            <Grid item xs={4}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  name="jobGrade"
                  label="Job Grade"
                  value={jobGrade}
                  onChange={handleJobGrade}
                />
              </FormControl>
            </Grid>
            <Grid item xs={8}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="department"
                  label="Department"
                  value={department}
                  onChange={handleDepartment}
                />
              </FormControl>
            </Grid>
            <Grid item xs={4}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  name="jobCategory"
                  label="Job Category"
                  value={jobCategory}
                  onChange={handleJobCategory}
                />
              </FormControl>
            </Grid>
            <Grid item xs={8}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  name="eMail"
                  label="EMail"
                  value={eMail}
                  onChange={handleEMail}
                />
              </FormControl>
            </Grid>
            <Grid item xs={4}>
              <StatusSelectInputEdit />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleEditClose} variant="contained" color="secondary">Cancel</Button>
          <Button variant="contained" type="submit" onClick={handleEditEmployee}>Save</Button>
        </DialogActions>
      </Dialog>
    );
  }

  function EmployeeToAddDialog() {
    const [badgeNumber, setBadgeNumber] = useState("");
    const [name, setName] = useState("");
    const [gender, setGender] = useState("");
    const [positionCategoryName, setPositionCategoryName] = useState("");
    const [jobPosition, setJobPosition] = useState("");
    const [jobGrade, setJobGrade] = useState("");
    const [jobCategory, setJobCategory] = useState("");
    const [department, setDepartment] = useState("");
    const [status, setStatus] = useState("");
    const [eMail, setEMail] = useState("");

    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);


    function GenderSelectInputAdd() {
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

      return (
        <div>
          <FormControl sx={{ m: 1, width: 1 }} >
            <InputLabel id="gender-label">Gender *</InputLabel>
            <Select
              required
              labelId="gender-label"
              label="Gender *"
              value={gender}
              onChange={handleGender}
              MenuProps={MenuProps}
            >
              {genders && genders.map((gender) => (
                <MenuItem
                  key={gender.id}
                  value={gender.name}
                >
                  {gender.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </div>
      );
    }

    function PositionCategorySelectInputAdd() {

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

      return (
        <div>
          <FormControl sx={{ m: 1, width: 1 }} >
            <InputLabel id="category-label">Category *</InputLabel>
            <Select
              required
              labelId="category-label"
              value={positionCategoryName}
              onChange={handlePositionCategory}
              label="Category *"
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

    function DepartmentNameSelectInputAdd() {

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

      return (
        <div>
          <FormControl sx={{ m: 1, width: 1 }} >
            <InputLabel id="department-label">Department/Branch *</InputLabel>
            <Select
              required
              labelId="department-label"
              value={department}
              onChange={handleDepartment}
              label="Department/Branch *"
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

    function StatusSelectInputAdd() {

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

      return (
        <div>
          <FormControl sx={{ m: 1, width: 1 }} >
            <InputLabel id="status-label">Status *</InputLabel>
            <Select
              required
              labelId="status-label"
              value={status}
              onChange={handleStatus}
              label="Status *"
              MenuProps={MenuProps}
            >
              {statuses && statuses.map((status) => (
                <MenuItem
                  key={status.id}
                  value={status.id}
                >
                  {status.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </div>
      );
    }

    const handleAddEmployee = (event) => {
      if (badgeNumber && name && positionCategoryName && gender && jobPosition && department && status) {
        const employeeToAdd: Employee = {
          id: '',
          badgeNumber: badgeNumber,
          name: name,
          positionCategoryName: positionCategoryName,
          gender: gender,
          jobPosition: jobPosition,
          jobGrade: jobGrade,
          department: department,
          jobCategory: jobCategory,
          eMail: eMail,
          status: status,
          inactive: false,
          createdDate: undefined,
          createdBy: '',
          uniformRequisitionCollection: [],
          positionCategory: undefined,
          page: '',
          rowsPerPage: ''
        };
        setErrorMessageHidden(false);
        requestResourceWithCredsPost(apiEmployeesOfficerCreateUrl, employeeToAdd).then(response => {
          //SUCCESS
          if (response) {
            setOpenAdd(false);
            setActionStatus(true);
            setOpenActionStatus(true);
            setIsLoading(true);
            const queryParams = {
              page: page,
              rowsPerPage: rowsPerPage
            };
            requestResourceWithCredsPost(apiEmployeesIndexUrl, queryParams).then(data => {
              // if (data) {
              setRows(data);
              requestResourceWithCredsPost(apiEmployeesCountUrl, queryParams).then(data => {
                setTotalCount(data);
                setIsLoading(false);
              });
              // }
            });
          }
          else {
            setOpenAdd(false);
            setActionStatus(false);
            setOpenActionStatus(true);
          }
        })
          .catch(error => {
            //ERROR
            setOpenAdd(false);
            setActionStatus(false);
            setOpenActionStatus(true);
          });
      }
      else {
        setErrorMessageHidden(true);
      }
    };

    const handleBadgeNumber = (event) => {
      setBadgeNumber(event.target.value);
    };

    const handleGender = (event) => {
      setGender(event.target.value);
    };

    const handlePositionCategory = (event) => {
      setPositionCategoryName(event.target.value);
    };

    const handleJobPosition = (event) => {
      setJobPosition(event.target.value);
    };

    const handleJobGrade = (event) => {
      setJobGrade(event.target.value);
    };

    const handleJobCategory = (event) => {
      setJobCategory(event.target.value);
    };

    const handleDepartment = (event) => {
      setDepartment(event.target.value);
    };

    const handleStatus = (event) => {
      setStatus(event.target.value);
    };

    const handleEMail = (event) => {
      setEMail(event.target.value);
    };

    const handleName = (event) => {
      setName(event.target.value);
    };

    return (
      <Dialog
        open={openAdd}
        onClose={handleAddClose}
      >
        <DialogTitle align='center'>
          <Grid item xs={12}><span>Add Employee</span></Grid>
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
                <TextField
                  required
                  name="badgeNumber"
                  label="Badge Number"
                  value={badgeNumber}
                  onChange={handleBadgeNumber}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="name"
                  label="Name"
                  value={name}
                  onChange={handleName}
                />
              </FormControl>
            </Grid>
            <Grid item xs={8}>
              <PositionCategorySelectInputAdd />
            </Grid>
            <Grid item xs={4}>
              <GenderSelectInputAdd />
            </Grid>
            <Grid item xs={8}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="jobPosition"
                  label="Job Position"
                  value={jobPosition}
                  onChange={handleJobPosition}
                />
              </FormControl>
            </Grid>
            <Grid item xs={4}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  name="jobGrade"
                  label="Job Grade"
                  value={jobGrade}
                  onChange={handleJobGrade}
                />
              </FormControl>
            </Grid>
            <Grid item xs={8}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="department"
                  label="Department"
                  value={department}
                  onChange={handleDepartment}
                />
              </FormControl>
            </Grid>
            <Grid item xs={4}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  name="jobCategory"
                  label="Job Category"
                  value={jobCategory}
                  onChange={handleJobCategory}
                />
              </FormControl>
            </Grid>
            <Grid item xs={8}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  name="eMail"
                  label="EMail"
                  value={eMail}
                  onChange={handleEMail}
                />
              </FormControl>
            </Grid>
            <Grid item xs={4}>
              <StatusSelectInputAdd />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleAddClose} variant="contained" color="secondary">Cancel</Button>
          <Button variant="contained" type="submit" onClick={handleAddEmployee}>Save</Button>
        </DialogActions>
      </Dialog>
    );
  }

  const handleName = (event) => {
    setName(event.target.value);
  };

  function ActionStatusDialog() {
    return (
      <Dialog open={openActionStatus} onClose={handleActionStatusClose}>
        <DialogTitle align='center'>Action Status</DialogTitle>
        <DialogContent>
          {actionStatus && <Alert severity="success">Action Success.</Alert>}
          {!actionStatus && <Alert severity="error">Action Failure.</Alert>}
        </DialogContent>
      </Dialog>
    );
  }

  const handleDownload = () => {
    try {
      setLoadingReport(true);
      const queryParams = {
        name: name,
        gender: selectedGender,
        status: selectedStatus,
        positionCategoryName: selectedPositionCategory,
        department: selectedDepartmentName,
        page: page,
        rowsPerPage: rowsPerPage
      };
      requestReports(apiEmployeesDownloadEntitledStaffReportUrl, queryParams)
        .then(data => {
          const binaryData = Buffer.from(data.byteArr);
          const blob = new Blob([binaryData.buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          setEntitledStaffReport(blob);
        }
        )
        .catch(error => {
          console.log(error);
        });
    } catch (error) {
    }
  };


  if (isLoading) {
    return <div><LinearProgress /></div>;
  }

  return (
    <>
      <div>

        <FormDialog />

        <EmployeeToAddDialog />

        <EmployeeToEditDialog />

        <ActionStatusDialog />
      </div>

      <div>
      </div>

      <Grid container spacing={2}>
        <Grid item xs={4}>
          <TextField fullWidth
            label="Name.."
            placeholder="Name"
            name="name"
            onChange={handleName} />
        </Grid>
        <Grid item xs={2}>
          <GenderSelectInput />
        </Grid>
        <Grid item xs={4}>
          <PositionCategorySelectInput />
        </Grid>
      </Grid>

      <Grid container spacing={2} paddingTop={2}>
        <Grid item xs={4}>
          <DepartmentNameSelectInput />
        </Grid>
        <Grid item xs={2}>
          <StatusSelectInput />
        </Grid>
        <Grid item xs>
          <Button variant="contained" onClick={handleDownload} disabled={loadingReport}>
            {loadingReport ? 'Downloading...' : 'Download Excel'}
          </Button>
        </Grid>
        <Grid item xs={2}>
          <Button variant="contained" type='button' endIcon={<Add />} onClick={() => handleAddButtonClick()} >Add</Button>
        </Grid>
      </Grid>

      <TableContainer>
        <Table sx={{ minWidth: 650 }}>
          <TableHead>
            {(
              <TableRow>
                <NoWrapPadTblCell>Badge Number</NoWrapPadTblCell>
                <NoWrapPadTblCell>Name</NoWrapPadTblCell>
                <NoWrapPadTblCell>Category</NoWrapPadTblCell>
                <PadTblCell>Gender</PadTblCell>
                <PadTblCell>Job Position</PadTblCell>
                <PadTblCell>Status</PadTblCell>
                <PadTblCell>Branch</PadTblCell>
                <PadTblCell>Action</PadTblCell>
              </TableRow>
            )}
          </TableHead>
          <TableBody>
            {rows && (rows.map(row => (
              <TableRow key={row.id}>
                <NoWrapPadTblCell>{row.badgeNumber}</NoWrapPadTblCell>
                <NoWrapPadTblCell>{row.name}</NoWrapPadTblCell>
                <NoWrapPadTblCell>{row.positionCategoryName}</NoWrapPadTblCell>
                <PadTblCell>{row.gender}</PadTblCell>
                <PadTblCell>{row.jobPosition}</PadTblCell>
                <PadTblCell>{row.inactive ? "Inactive" : "Active"}</PadTblCell>
                <PadTblCell>{row.department}</PadTblCell>
                <PadTblCell>
                  <Grid container spacing={1}>
                    <Grid item>
                      <Button variant="contained" type='button' onClick={() => handleUniformButtonClick(row)} size='small'>Uniform</Button>
                    </Grid>
                    <Grid item>
                      <Button variant="contained" type='button' onClick={() => handleEditButtonClick(row)} size='small'>Edit</Button>
                    </Grid>
                  </Grid>
                </PadTblCell>
              </TableRow>
            )))}
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



    </>
  );
}
