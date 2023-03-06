import React from 'react';
import { Table } from 'antd';
import type { ColumnsType } from 'antd/es/table';

function MiddleWareTable():JSX.Element{
  interface DataType {
    key: string;
    category: string;
    middleWare: string;
  }

  const columns: ColumnsType<DataType> = [
    {
      title: 'Category',
      dataIndex: 'category',
      key: 'category'
    },
    {
      title: 'MiddleWare',
      dataIndex: 'middleWare',
      key: 'middleWare',
    }
  ];

  const data: DataType[] = [
    {
      key: '1',
      category: 'hhhhhhh',
      middleWare: 'New York No. 1 Lake Park',
    },
    {
      key: '2',
      category: 'John Brown',
      middleWare: 'New York No. 1 Lake Park',
    }
  ];

  return(
      <Table columns={columns} dataSource={data} />
  )
}

export default MiddleWareTable;