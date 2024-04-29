import React, { useEffect, useRef } from 'react';
import { getRandomInt } from '../../utils/random';
import * as THREE from 'three';
import StarMesh from './StarMesh';
import { Canvas } from '@react-three/fiber';
import Controls from './Controls';
import GLBModel from './GLBModel';
import Lights from './Lights';
import FloorMesh from './FloorMesh';
import { GetStars } from '../../apis/StarApis';
import Loading from '../common/Loading/Loading';
import { useQuery } from '@tanstack/react-query';

type Props = {};

const MainCanvas = (props: Props) => {
  function genBackgroundStars() {
    const stars = [];
    for (let i = 0; i < 300; i++) {
      const size = getRandomInt(50, 60);
      const pos = new THREE.Vector3(
        getRandomInt(-5000, 5000),
        getRandomInt(-5000, 5000),
        getRandomInt(-5000, 5000),
      );
      stars.push(<StarMesh key={i} position={pos} size={size} />);
    }
    // for (let i = 0; i < 30; i++) {
    //   const size = getRandomInt(50, 60);
    //   const pos = new THREE.Vector3(
    //     getRandomInt(-500, 500),
    //     getRandomInt(-500, 500),
    //     getRandomInt(-500, 500),
    //   );
    //   stars.push(<StarMesh key={i} position={pos} size={size} />);
    // }
    return stars;
  }

  const {
    isLoading: isStarsLoading,
    data: starData,
    isError: isStarsError,
    refetch: getStarsRefetch,
  } = useQuery({
    queryKey: ['get-stars'],
    queryFn: GetStars,
  });

  if (isStarsLoading) {
    return (
      <>
        <Loading />
      </>
    );
  }

  return (
    <Canvas
      gl={{ antialias: true }}
      scene={{ background: new THREE.Color(0x000000) }}
      camera={{
        fov: 100,
        position: [
          -0.5 / Math.sqrt(3),
          -0.5 / Math.sqrt(3),
          -0.5 / Math.sqrt(3),
          //   100, 100, 100,
          //   0, 200, 1000,
        ],
        // rotation: [0, 0, 0],
        far: 100000,
      }}
    >
      <Controls />
      <Lights />
      {starData?.data.map((star: any) => (
        <StarMesh
          key={star._id}
          position={
            new THREE.Vector3(
              star.calX * 15000,
              star.calY * 15000,
              star.calZ * 15000,
            )
          }
          size={getRandomInt(30, 40)}
        />
      ))}
      {/* {genBackgroundStars()} */}
      <FloorMesh />
      {/* <GLBModel /> */}
    </Canvas>
  );
};

export default MainCanvas;