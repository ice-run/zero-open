import { http } from "@/utils/http";
import type { Request, Response } from "@/api";

/** info 文件信息 传入文件 id，查询文件信息 */
export async function fileInfo(
  request: Request<FileParam>
): Promise<Response<FileData>> {
  return http.post<Request<FileParam>, Response<FileData>>(
    `/base/api/file-info`,
    {
      data: request
    }
  );
}

export type FileData = {
  /** id id */
  id: string;
  /** code 文件 code */
  code: string;
  /** name 文件名 */
  name: string;
  /** origin 源文件名 */
  origin: string;
  /** type 文件类型 */
  type: string;
  /** size 文件大小 */
  size: string;
  /** path 路径 */
  path: string;
};

export type FileParam = {
  /** id 文件 ID */
  id: string;
  /** code 文件 code */
  code?: string;
};
