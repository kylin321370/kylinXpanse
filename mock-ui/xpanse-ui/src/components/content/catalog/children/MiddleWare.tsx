import React from 'react';
import {Space, Tree} from 'antd';
import {DataNode, TreeProps} from "antd/es/tree";
import Icon, {CloudOutlined, MessageOutlined, NodeIndexOutlined} from "@ant-design/icons";
import {ReactComponent as RancherSvg} from "./img/rancher_icon.svg";
import {ReactComponent as ActivemqSvg} from './img/activemq_icon.svg';
import axios from "axios";


function MiddleWare(): JSX.Element {
  const responseBody:any = axios.get('http://192.168.0.97:8080/xpanse/register/service/3b6510ba-fe1c-4584-8fd3-2cb6381d4dbc').then(
      function (response){
        console.log(response)
        console.log("data: "+response.data.data.id)
        console.log("state: "+response.data.data.serviceState)
        console.log("ocl****: "+response.data.data.ocl.version)
      }
  )

  const treeData: DataNode[] = [
    {
      title: <Space><CloudOutlined/>Compute</Space>,
      key: 'compute',
      selectable: false,
      checkable: false,
      children: [
        {title: <Space><MessageOutlined/>Kubernetes</Space>,
          key: 'kubernetes',
          selectable: true,
          checkable: true,
        },
        {title: <Space><Icon component={RancherSvg}/>Rancher</Space>, key: 'rancher'}
      ],
    },
    {
      title: <Space><NodeIndexOutlined/>Integration</Space>,
      key: 'integration',
      selectable: false,
      checkable: false,
      children: [
        {title: <Space><Icon component={ActivemqSvg}/>ActiveMQ</Space>, key: 'activemq'}
      ]
    },
    {
      title: 'parent 1-0',
      key: '0-0-0',
      disabled: true,
      children: [
        {
          title: 'leaf',
          key: '0-0-0-0',
          disableCheckbox: true,
        },
        {
          title: 'leaf',
          key: '0-0-0-1',
        },
      ],
    },
    {
      title: 'parent 1-1',
      key: '0-0-1',
      children: [{title: <span style={{color: '#1890ff'}}>sss</span>, key: '0-0-1-0'}],
    },
  ];

  const onSelect: TreeProps['onSelect'] = (selectedKeys, info) => {
    console.log('selected', selectedKeys, info);
  };

  const onCheck: TreeProps['onCheck'] = (checkedKeys, info) => {
    console.log('onCheck', checkedKeys, info);
  };

  const a  = () =>{
    axios.get('http://localhost:3000/xpanse/').then(

    )
  }

  return (
      <>
        <Tree
            checkable
            defaultExpandedKeys={['0-0-0', '0-0-1']}
            defaultSelectedKeys={['0-0-0', '0-0-1']}
            defaultCheckedKeys={['0-0-0', '0-0-1']}
            onSelect={onSelect}
            onCheck={onCheck}
            treeData={treeData}
        />

        <div>
          responseBody
        </div>
      </>
  );
}

export default MiddleWare;