/**
 * OpenAPI definition
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * OpenAPI spec version: v0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import { SecurityGroup } from './SecurityGroup';
import { Subnet } from './Subnet';
import { Vpc } from './Vpc';

/**
 * The network resources for the managed service
 */
export class Network {
  /**
   * The list of vpc in the network
   */
  'vpc': Array<Vpc>;
  /**
   * The list of subnets in the network for the @vpc
   */
  'subnets': Array<Subnet>;
  /**
   * The list of security groups for the VMs
   */
  'securityGroups': Array<SecurityGroup>;

  static readonly discriminator: string | undefined = undefined;

  static readonly attributeTypeMap: Array<{ name: string; baseName: string; type: string; format: string }> = [
    {
      name: 'vpc',
      baseName: 'vpc',
      type: 'Array<Vpc>',
      format: '',
    },
    {
      name: 'subnets',
      baseName: 'subnets',
      type: 'Array<Subnet>',
      format: '',
    },
    {
      name: 'securityGroups',
      baseName: 'securityGroups',
      type: 'Array<SecurityGroup>',
      format: '',
    },
  ];

  static getAttributeTypeMap() {
    return Network.attributeTypeMap;
  }

  public constructor() {}
}
