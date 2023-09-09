package com.ead.project.dreamer.presentation.server.order.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ead.project.dreamer.presentation.server.order.ServerOrderFragment
import javax.annotation.Nonnull

class ServerOrderViewPagerAdapter(@Nonnull fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ServerOrderFragment().apply {
                description = "El modo automático entra en funcionamiento según la disposición de los servidores disponibles, siguiendo un orden específico. Por ejemplo, en el escenario por defecto, el servidor principal es 1Fichier. La aplicación implementará el proceso destinado a localizar el recurso, siempre y cuando el recurso no haya sido eliminado o retirado. Esta búsqueda se efectuará de manera secuencial en cada uno de los servidores, hasta que el recurso sea identificado o hasta que no haya más servidores disponibles. Este procedimiento será activado exclusivamente cuando el reproductor se encuentre en el Modo Interno."
                optionOrder = ServerOrderFragment.INTERNAL_SERVERS
            }
            1 -> ServerOrderFragment().apply {
                description = "El modo automático entra en funcionamiento según la disposición de los servidores disponibles, siguiendo un orden específico. Por ejemplo, en el escenario por defecto, el servidor principal es Okru. La aplicación implementará el proceso destinado a localizar el recurso, siempre y cuando el recurso no haya sido eliminado o retirado. Esta búsqueda se efectuará de manera secuencial en cada uno de los servidores, hasta que el recurso sea identificado o hasta que no haya más servidores disponibles. Este procedimiento será activado exclusivamente cuando el reproductor se encuentre en el Modo Externo"
                optionOrder = ServerOrderFragment.EXTERNAL_SERVERS
            }
            2 -> ServerOrderFragment().apply {
                description = "El modo automático entra en funcionamiento según la disposición de los servidores disponibles, siguiendo un orden específico. Por ejemplo, en el escenario por defecto, el servidor principal es 1Fichier. La aplicación implementará el proceso destinado a localizar el recurso, siempre y cuando el recurso no haya sido eliminado o retirado. Esta búsqueda se efectuará de manera secuencial en cada uno de los servidores, hasta que el recurso sea identificado o hasta que no haya más servidores disponibles. Este procedimiento será activado exclusivamente cuando se este descargando."
                optionOrder = ServerOrderFragment.DOWNLOAD_SERVERS
            }
            else -> ServerOrderFragment()
        }
    }
}