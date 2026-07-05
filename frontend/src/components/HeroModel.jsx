import { Canvas, useFrame } from '@react-three/fiber'
import { useGLTF, Environment, Center } from '@react-three/drei'
import { Suspense, useRef, useMemo } from 'react'

function Model({ dragState }) {
  const { scene } = useGLTF('/static/models/logo.gltf')
  const cloned = useMemo(() => scene.clone(true), [scene])
  const ref = useRef(null)

  useFrame(({ clock }) => {
    if (!ref.current) return
    const t = clock.getElapsedTime()
    const ds = dragState.current

    if (ds.active) {
      ds.rotX += ds.velX
      ds.rotY += ds.velY
      ds.velX *= 0.6
      ds.velY *= 0.6
      ref.current.rotation.x = ds.rotX
      ref.current.rotation.y = ds.rotY
      ref.current.rotation.z = 0
    } else {
      ds.rotX += (0 - ds.rotX) * 0.07
      ds.rotY += (0 - ds.rotY) * 0.07
      ref.current.rotation.x = ds.rotX + Math.sin(t * 0.9) * 0.04
      ref.current.rotation.y = ds.rotY + Math.sin(t * 0.7) * 0.03
      ref.current.rotation.z = Math.sin(t * 1.3) * 0.055
    }
  })

  return (
    <Center precise>
      <primitive ref={ref} object={cloned} scale={3.2} />
    </Center>
  )
}

export function HeroModel({ dragState }) {
  return (
    <Canvas
      camera={{ position: [0, 0, 3.5], fov: 45 }}
      gl={{ antialias: true, alpha: true, powerPreference: 'high-performance' }}
      style={{ width: '100%', height: '100%', background: 'transparent' }}
    >
      <Suspense fallback={null}>
        <Model dragState={dragState} />
        <Environment preset="night" />
      </Suspense>
      <directionalLight position={[2, 3, 2]} intensity={2.0} color="#C4B5FD" />
      <directionalLight position={[-2, 1, -1]} intensity={0.8} color="#7E4DD9" />
      <ambientLight intensity={0.3} />
      <pointLight position={[0, 3, 0]} intensity={1.0} color="#A78BFA" />
    </Canvas>
  )
}

useGLTF.preload('/static/models/logo.gltf')

export default HeroModel
