import { publicRequest } from '../hooks/requestMethods';
import Swal from 'sweetalert2';

export const MakeMyConstellationApi = async (
  userConstellationData: UserConstellationData,
) => {
  try {
    const res = await publicRequest.post(
      `user-constellation/create`,
      userConstellationData,
    );

    await Swal.fire({
      title: '성공!',
      text: '나만의 별자리가 성공적으로 생성되었습니다.',
      icon: 'success',
      confirmButtonText: '확인',
      customClass: {
        container: 'my-swal'
      }
    });

    return res.data;
  } catch (error) {
    console.log(error);
    throw new Error('나만의 별자리 만들기 api 에러');
  }
};

export const GetUserConstellationLinkApi = async (userId: string) => {
  return await publicRequest.get('user-constellation/link', {
    params: {
      userId: userId,
    },
  });
};

export const DeleteUserConstellation = async (
  deleteData: deleteMyConstellationApitype,
) => {
  return await publicRequest
    .delete('user-constellation/delete', {
      params: deleteData,
    })
    .then((res) => res.data);
};

export const GetUserConstellation = async (userId: string) => {
  return await publicRequest.get('/user-constellation/all', {
    params: {
      userId: userId,
    },
  });
};

export const GetMyconstDetail = async (userId: string, constellationId: string) => {
  return await publicRequest.get('/user-constellation', {
    params: {
      userId:userId,
      constellationId: constellationId,
    },
  });
};
