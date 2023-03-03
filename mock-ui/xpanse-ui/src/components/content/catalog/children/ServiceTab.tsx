import React from 'react';
import { Table } from 'antd';
import type { ColumnsType, TableProps } from 'antd/es/table';

  interface DataType {
    key: React.Key;
    Category: string;
    MiddleWare: string;
  }

  const columns: ColumnsType<DataType> = [
    {
      title: 'Category',
      dataIndex: 'Category',
      filters: [
        {
          text: 'Joe',
          value: 'Joe',
        },
        {
          text: 'Category 1',
          value: 'Category 1',
          children: [
            {
              text: 'Yellow',
              value: 'Yellow',
            },
            {
              text: 'Pink',
              value: 'Pink',
            },
          ],
        },
        {
          text: 'Category 2',
          value: 'Category 2',
          children: [
            {
              text: 'Green',
              value: 'Green',
            },
            {
              text: 'Black',
              value: 'Black',
            },
          ],
        },
      ],
      filterMode: 'tree',
      filterSearch: true,
      /*onFilter: (value: string, record) => record.name.includes(value),*/
      width: '20%',
    },
    {
      title: 'MiddleWare',
      dataIndex: 'MiddleWare',
      filters: [
        {
          text: 'London',
          value: 'London',
        },
        {
          text: 'New York',
          value: 'New York',
        },
      ],
     /* onFilter: (value: string, record) => record.address.startsWith(value),*/
      filterSearch: true,
      width: '30%',
    },
  ];

  const data: DataType[] = [
    {
      key: '1',
      Category: 'Provider',
      MiddleWare: 'New York No. 1 Lake Park',
    },
    {
      key: '2',
      Category: 'Service Version',
      MiddleWare: 'V1.0',
    },
    {
      key: '3',
      Category: 'Billing Mode',
      MiddleWare: 'Monthly Per Service Instance',
    },
    {
      key: '4',
      Category: 'Regullar Pricing',
      MiddleWare: '￥140.00',
    },
    {
      key: '5',
      Category: 'Register Time',
      MiddleWare: '2022-08-26 T08:25:15:208Z',
    },
    {
      key: '6',
      Category: 'Status',
      MiddleWare: '2022-08-26 T08:25:15:208Z',
    },
    {
      key: '7',
      Category: 'Flavors',
      MiddleWare: 'London No. 2 Lake Park',
    },
  ];

  const onChange: TableProps<DataType>['onChange'] = (pagination, filters, sorter, extra) => {
    console.log('params', pagination, filters, sorter, extra);
  };

  const ServicesTab: React.FC = () => <Table columns={columns} dataSource={data} onChange={onChange} />;

export default ServicesTab;