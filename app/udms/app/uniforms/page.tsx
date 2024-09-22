'use client'
import { Alert, AlertTitle, Button, Dialog, DialogActions, DialogContent, DialogTitle, Divider, FormControl, Grid, InputLabel, LinearProgress, MenuItem, OutlinedInput, Paper, Select, SelectChangeEvent, TableCell, TableHead, TextField, Typography } from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import { styled } from '@mui/material/styles';
import { useEffect, useState } from 'react';
import { refreshInactiveToken, requestResourceWithCredsPost } from "../services/ServiceUtil";

export default function Page() {
  const [page, setPage] = useState(0);
  const [totalCount, setTotalCount] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(4);
  const [rows, setRows] = useState<Employee[]>([]);
  const [name, setName] = useState("");
  const [imsItems, setImsItems] = useState<ImsItem[]>([]);
  const [positionCategories, setPositionCategories] = useState<PositionCategory[]>([]);
  const [selectedPositionCategory, setSelectedPositionCategory] = useState('');
  const [selectedGender, setSelectedGender] = useState('');
  const [open, setOpen] = useState(false);
  const [employeeUniformItemRows, setEmployeeUniformItemRows] = useState<EmployeeUniformItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedEmployee, setSelectedEmployee] = useState<Employee>();
  const [openActionStatus, setOpenActionStatus] = useState(false);
  const [actionStatus, setActionStatus] = useState('');

  // const redirectURIApi = "http://localhost:8082/udmsapi";
  const redirectURIApi = "https://aps3.zemenbank.com/TestUDMS/udmsapi";
  // const redirectURIApi = "https://aps3.zemenbank.com/UDMS/udmsapi";


  let apiRequestCsrIndexUrl = redirectURIApi + "/uniforms/csr/index";
  let apiRequestCommonPositionCategories = redirectURIApi + "/uniforms/common/positionCategories";
  let apiRequestCsrItems = redirectURIApi + "/uniforms/csr/items";
  let apiRequestOfficerCreateUrl = redirectURIApi + "/uniforms/csr/createrequest";

  const genders = [
    { id: 'F', name: 'F' },
    { id: 'M', name: 'M' },
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
        positionCategoryName: selectedPositionCategory,
        page: page,
        rowsPerPage: rowsPerPage
      };
      await refreshInactiveToken();
      const dataIndex = await requestResourceWithCredsPost(apiRequestCsrIndexUrl, queryParams);
      setRows(dataIndex);
      const dataPosCategory = await requestResourceWithCredsPost(apiRequestCommonPositionCategories, queryParams);
      setPositionCategories(dataPosCategory);
      setIsLoading(false);
    };
    fetchData();
  }, [page, rowsPerPage, name, selectedGender, selectedPositionCategory]);



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
      setPage(0);
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

  const handleClose = () => {
    setOpen(false);
  };

  const handleActionStatusClose = () => {
    setOpenActionStatus(false);
  };

  const handleName = (event) => {
    setName(event.target.value);
  };


  const handleRequestButtonClick = (row) => {
    requestResourceWithCredsPost(apiRequestCsrItems, []).then(data => {
      setImsItems(data);
      setSelectedEmployee(row);
      setOpen(true);
    });
  };

  function RequestToAddDialog() {
    const [selectedItem, setSelectedItem] = useState<ImsItem>(null);
    const [uniformRequisitionRows, setUniformRequisitionRows] = useState<UniformRequisition[]>([]);
    const [uniformRequisitionDetailRows, setUniformRequisitionDetailRows] = useState<UniformRequisitionDetail[]>([]);
    const [size, setSize] = useState("");
    const [isErrorMessageHidden, setErrorMessageHidden] = useState(false);

    const handleItemChange = (event: SelectChangeEvent) => {
      const selectedMaterial = imsItems.find(item => item.id === event.target.value);
      setSelectedItem(selectedMaterial);
    };

    function ImsItemSelectInputAdd() {

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
            <InputLabel id="item-label">Item *</InputLabel>
            <Select
              labelId="item-label"
              value={selectedItem ? selectedItem.id : ''}
              onChange={handleItemChange}
              label="Item *"
              MenuProps={MenuProps}
            >
              {imsItems && imsItems.map((imsItem) => (
                <MenuItem
                  key={imsItem.id}
                  value={imsItem.id}
                >
                  {imsItem.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </div>
      );
    }

    const handleAddToList = (event) => {
      if (selectedItem && size) {
        setErrorMessageHidden(false);
        let uniformRequisitionDetail: UniformRequisitionDetail = {
          id: "",
          imsItemId: selectedItem.id,
          itemName: selectedItem.name,
          size: size,
          uniformIssueDetailCollection: [],
          uniformRequisitionId: undefined,
          lastIssueDate: undefined,
          nextIssueDate: undefined,
          approvedBy: '',
          approvedDate: undefined
        }
        uniformRequisitionDetailRows.push(uniformRequisitionDetail);
        setSelectedItem(null);
        setSize("");
        const tempSelectedItem = imsItems.find(item => item.id === selectedItem.id);
        let indexToRemove = imsItems.indexOf(tempSelectedItem);
        if (indexToRemove !== -1) {
          imsItems.splice(indexToRemove, 1);
        }
      }
      else {
        setErrorMessageHidden(true);
      }
    };

    const handleSubmitRequestForApproval = async (event) => {
      let uniformRequisition: UniformRequisition = {
        id: "",
        department: selectedEmployee.department,
        createdBy: '',
        createdDate: undefined,
        approvedBy: '',
        approvedDate: undefined,
        uniformRequisitionDetailCollection: uniformRequisitionDetailRows,
        employeeId: undefined,
        luStatusId: undefined,
        remark: ''
      }

      uniformRequisitionRows.push(uniformRequisition);
      const employee: Employee = {
        id: selectedEmployee.id,
        badgeNumber: '',
        name: '',
        gender: '',
        uniformRequisitionCollection: uniformRequisitionRows,
        positionCategory: undefined,
        positionCategoryName: '',
        jobPosition: '',
        jobGrade: '',
        jobCategory: '',
        department: '',
        inactive: false,
        status: '',
        eMail: '',
        createdBy: '',
        createdDate: undefined,
        page: '',
        rowsPerPage: ''
      }

      if (employee.uniformRequisitionCollection && employee.uniformRequisitionCollection?.length > 0) {
        const response = await requestResourceWithCredsPost(apiRequestOfficerCreateUrl, employee);
        //SUCCESS
        setOpen(false);
        setActionStatus('form-success');
        setOpenActionStatus(true);
        setIsLoading(true);
        try {
          if (response) {
            const data = await requestResourceWithCredsPost(apiRequestCommonPositionCategories, []);
            const queryParams = {
              name: name,
              gender: selectedGender,
              positionCategoryName: selectedPositionCategory,
              page: page,
              rowsPerPage: rowsPerPage
            };
            setPositionCategories(data);
            const dataIndex = await requestResourceWithCredsPost(apiRequestCsrIndexUrl, queryParams);
            if (dataIndex) {
              setRows(dataIndex);
              setIsLoading(false);
            }
          }
          else {
            setOpen(false);
            setActionStatus('form-fail');
            setOpenActionStatus(true);
          }
        }
        catch (error) {
          //ERROR
          setOpen(false);
          setActionStatus('form-fail');
          setOpenActionStatus(true);
        }
      }
    };

    const handleSizeChange = (event) => {
      setSize(event.target.value);
    };


    return (
      <Dialog open={open}
        onClose={handleClose}
      >
        <DialogTitle align='center'>
          <Grid item xs={12}><span>Request</span></Grid>
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
                  {selectedEmployee?.name}
                </Paper>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <ImsItemSelectInputAdd />
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }}>
                <TextField
                  required
                  name="size"
                  label="Size"
                  value={size}
                  onChange={handleSizeChange}
                />
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <FormControl sx={{ m: 1, width: 1 }} size="small">
                <Button variant="contained" type='submit' onClick={handleAddToList}>
                  Add To List
                </Button>
              </FormControl>
            </Grid>
            <Grid item xs={12}><Divider>Item List</Divider></Grid>
            <TableContainer>
              <Table>
                <TableHead>
                  {(
                    <TableRow>
                      <TableCell>Item</TableCell>
                      <TableCell>Size</TableCell>
                    </TableRow>
                  )}
                </TableHead>
                <TableBody>
                  {uniformRequisitionDetailRows && uniformRequisitionDetailRows.map(row => (
                    <TableRow key={row.imsItemId}>
                      <TableCell>{row.itemName}</TableCell>
                      <TableCell>{row.size}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button variant="contained" type="submit" onClick={handleSubmitRequestForApproval}>Submit For Approval</Button>
        </DialogActions>
      </Dialog>
    );
  }

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

  if (isLoading) {
    return <div><LinearProgress /></div>;
  }


  return (
    <>
      <div>
        <RequestToAddDialog />
        <ActionStatusDialog />
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
              <TableRow>
                <NoWrapPadTblCell>{row.badgeNumber}</NoWrapPadTblCell>
                <NoWrapPadTblCell>{row.name}</NoWrapPadTblCell>
                <NoWrapPadTblCell>{row.positionCategoryName}</NoWrapPadTblCell>
                <PadTblCell>{row.gender}</PadTblCell>
                <PadTblCell>{row.jobPosition}</PadTblCell>
                <PadTblCell>{row.inactive ? "Inactive" : "Active"}</PadTblCell>
                <PadTblCell>{row.department}</PadTblCell>
                <PadTblCell>
                  <Grid container spacing={4}>
                    <Grid item xs={6}>
                      <Button variant="contained" type='button' onClick={() => handleRequestButtonClick(row)} size='small'>Request</Button>
                    </Grid>
                  </Grid>
                </PadTblCell>
              </TableRow>
            )))}
          </TableBody>
        </Table>
      </TableContainer>
    </>
  );

}
